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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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
		try {
			discussionRoom = discussionRepository.findByDiscussionId(chatRoomId).get();
		}catch (NoSuchElementException exception){
			System.out.println("No discussion room found for discussion id");
		}
		return discussionRoom;
	}

	@Override
	public Discussion join(DiscussionUser joiningUser, Discussion chatRoom) {
		//chatRoom.addUser(joiningUser);
		//discussionRepository.save(chatRoom);

		sendPublicMessage(SystemMessages.welcome(chatRoom.getDiscussionId(), joiningUser.getUsername()));
		updateConnectedUsersViaWebSocket(chatRoom);
		verifyAndDeleteOpponents(chatRoom);
		return chatRoom;
	}

	private void verifyAndDeleteOpponents(Discussion discussionRoom) {
		System.out.println("Going to delete from opponents for discussion Id: "+discussionRoom.getDiscussionId());
		if(MAX_OPPONENT_ALLOWED <= discussionRoom.getDiscussionUsers().size()) {
			opponentRepository.deleteByDiscussionRoomId(discussionRoom.getDiscussionId());
			System.out.println("deleted from opponents for discussion Id: "+discussionRoom.getDiscussionId());
			System.out.println("Verifying deletion from opponents for discussion Id: "+discussionRoom.getDiscussionId());
			try{
				opponentRepository.findByDiscussionRoomId(discussionRoom.getDiscussionId()).get();
			}catch (NoSuchElementException exception){
				System.out.println("successfully deleted from opponents for discussion Id: "+discussionRoom.getDiscussionId());
				return;
			}
			System.out.println("unable to delete from opponents for discussion Id: "+discussionRoom.getDiscussionId());
		}else{
			System.out.println("Room for more opponents for discussion Id: "+discussionRoom.getDiscussionId());
		}
	}

	@Override
	public Discussion leave(DiscussionUser leavingUser, Discussion chatRoom) {
		sendPublicMessage(SystemMessages.goodbye(chatRoom.getDiscussionId(), leavingUser.getUsername()));
		
		//chatRoom.removeUser(leavingUser);
		//discussionRepository.save(chatRoom);
		
		updateConnectedUsersViaWebSocket(chatRoom);
		return chatRoom;
	}

	@Override
	public void sendPublicMessage(InstantMessage instantMessage) {
		System.out.println("MessageType is: "+instantMessage.toString());
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
				Destinations.Discussion.connectedUsers(chatRoom.getDiscussionId()),
				chatRoom.getDiscussionUsers());
	}

	@Override
	public boolean deleteById(String id) {
		discussionRepository.deleteByDiscussionId(id);
		Discussion discussion =null;
		try {
			discussion = discussionRepository.findByDiscussionId(id).get();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/*@Override
	public List<Discussion> findByUsername(String username){
		return discussionRepository.findByUsername(username);
	}*/
	
}
