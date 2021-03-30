package com.hark.services.impl;

import com.hark.model.Discussion;
import com.hark.model.DiscussionUser;
import com.hark.model.InstantMessage;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.OpponentRepository;
import com.hark.services.DiscussionService;
import com.hark.services.InstantMessageService;
import com.hark.utils.Destinations;
import com.hark.utils.SystemMessages;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
		Discussion discussionRoom = null;
		List<Discussion> discussions = discussionRepository.findByDiscussionId(chatRoomId);
		if(CollectionUtils.isNotEmpty(discussions)){
			discussionRoom = discussions.get(0);
		}
		return discussionRoom;
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
			opponentRepository.deleteByDiscussionRoomId(discussionRoom.getDiscussionId());
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
		discussionRepository.deleteByDiscussionId(id);
		List<Discussion> discussions =null;
		try {
			discussions = discussionRepository.findByDiscussionId(id);
			if(CollectionUtils.isEmpty(discussions)){
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/*@Override
	public List<Discussion> findByUsername(String username){
		return discussionRepository.findByUsername(username);
	}*/
	
}
