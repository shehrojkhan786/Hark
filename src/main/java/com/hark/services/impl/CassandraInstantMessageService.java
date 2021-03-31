package com.hark.services.impl;

import java.util.List;

import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hark.model.Discussion;
import com.hark.model.InstantMessage;
import com.hark.repositories.InstantMessageRepository;
import com.hark.services.DiscussionService;
import com.hark.services.InstantMessageService;

@Service
public class CassandraInstantMessageService implements InstantMessageService {

	@Autowired
	private InstantMessageRepository instantMessageRepository;

	@Autowired
	private DiscussionService chatRoomService;

	@Override
	public void appendInstantMessageToConversations(InstantMessage instantMessage) {
		System.out.println("current Message deetails are: "+instantMessage.toString());
		if (instantMessage.isFromAdmin() || instantMessage.isPublic()) {
			Discussion chatRoom = chatRoomService.findById(instantMessage.getChatRoomId());
			System.out.println("MessageType is in appendInstantMessageToConversion: "+instantMessage.getChatMessageType());
			chatRoom.getDiscussionUsers().forEach(connectedUser -> {
				instantMessage.setUsername(connectedUser.getUsername());
				instantMessageRepository.save(instantMessage);
			});
		} else {
			instantMessage.setUsername(instantMessage.getFromUser());
			instantMessageRepository.save(instantMessage);

			instantMessage.setUsername(instantMessage.getToUser());
			instantMessageRepository.save(instantMessage);
		}
	}

	@Override
	public List<InstantMessage> findAllInstantMessagesFor(String username, String chatRoomId) {
		return instantMessageRepository.findInstantMessagesByUsernameAndChatRoomId(username, chatRoomId);
	}
}
