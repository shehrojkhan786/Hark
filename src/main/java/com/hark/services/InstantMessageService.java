package com.hark.services;

import java.util.List;

import com.hark.model.InstantMessage;

public interface InstantMessageService {
	
	void appendInstantMessageToConversations(InstantMessage instantMessage);
	List<InstantMessage> findAllInstantMessagesFor(String username, String chatRoomId);
}
