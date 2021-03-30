package com.hark.services.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hark.model.Discussion;
import com.hark.model.DiscussionUser;
import com.hark.model.InstantMessage;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.OpponentRepository;
import com.hark.services.DiscussionService;
import com.hark.services.InstantMessageService;
import com.hark.utils.Destinations;
import com.hark.utils.SystemMessages;

@Service
public class RedisDiscussionRoomService implements DiscussionService {
	final private int MAX_OPPONENT_ALLOWED=2;

	@Autowired
	private SimpMessagingTemplate webSocketMessagingTemplate;

	@Autowired
	private DiscussionRepository discussionRepository;
	
	@Autowired
	private InstantMessageService instantMessageService;
	
	@Autowired
	private OpponentRepository opponentRepository;

	@Override
	public Discussion save(Discussion chatRoom) {
		return discussionRepository.save(chatRoom);
	}

	@Override
	public Discussion findById(String chatRoomId) {
		return discussionRepository.findById(chatRoomId).orElseThrow();
	}

	@Override
	public Discussion join(DiscussionUser joiningUser, Discussion chatRoom) {
		chatRoom.addUser(joiningUser);
		discussionRepository.save(chatRoom);

		sendPublicMessage(SystemMessages.welcome(String.valueOf(chatRoom.getId()), joiningUser.getUsername()));
		updateConnectedUsersViaWebSocket(chatRoom);
		verifyAndDeleteOpponents(chatRoom);
		return chatRoom;
	}

	private void verifyAndDeleteOpponents(Discussion discussionRoom) {
		if(MAX_OPPONENT_ALLOWED <= discussionRoom.getUsers().size()) {
			opponentRepository.deleteByDiscussionRoomId(discussionRoom.getId());
		}
	}

	@Override
	public Discussion leave(DiscussionUser leavingUser, Discussion chatRoom) {
		sendPublicMessage(SystemMessages.goodbye(String.valueOf(chatRoom.getId()), leavingUser.getUsername()));
		
		chatRoom.removeUser(leavingUser);
		discussionRepository.save(chatRoom);
		
		updateConnectedUsersViaWebSocket(chatRoom);
		return chatRoom;
	}

	@Override
	public void sendPublicMessage(InstantMessage instantMessage) {
		System.out.println("MessageType is: "+instantMessage.getChatMessageType());
		webSocketMessagingTemplate.convertAndSend(
				Destinations.Discussion.publicMessages(instantMessage.getChatRoomId()),
				instantMessage);

		instantMessageService.appendInstantMessageToConversations(instantMessage);
	}

	@Override
	public void sendPrivateMessage(InstantMessage instantMessage) {
		webSocketMessagingTemplate.convertAndSendToUser(
				instantMessage.getToUser(),
				Destinations.Discussion.privateMessages(instantMessage.getChatRoomId()), 
				instantMessage);
		
		webSocketMessagingTemplate.convertAndSendToUser(
				instantMessage.getFromUser(),
				Destinations.Discussion.privateMessages(instantMessage.getChatRoomId()), 
				instantMessage);

		instantMessageService.appendInstantMessageToConversations(instantMessage);
		
	}

	@Override
	public List<Discussion> findAll() {
		return (List<Discussion>) discussionRepository.findAll();
	}
	
	private void updateConnectedUsersViaWebSocket(Discussion chatRoom) {
		webSocketMessagingTemplate.convertAndSend(
				Destinations.Discussion.connectedUsers(String.valueOf(chatRoom.getId())),
				chatRoom.getUsers());
	}

	@Override
	public boolean deleteById(String id) {
		discussionRepository.deleteById(id);
		Optional<Discussion> discussion = discussionRepository.findById(id);
		try{
			discussion.get();
		}catch (NoSuchElementException e) {
			return true;
		}
		return false;
	}

	/*@Override
	public List<Discussion> findByUsername(String username){
		return discussionRepository.findByUsername(username);
	}*/
	
}
