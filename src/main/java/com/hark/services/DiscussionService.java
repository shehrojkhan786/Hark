package com.hark.services;

import java.util.List;

import com.hark.model.Discussion;
import com.hark.model.DiscussionUser;
import com.hark.model.InstantMessage;

public interface DiscussionService {
	
	Discussion save(Discussion chatRoom);
	Discussion findById(String chatRoomId);
	Discussion join(DiscussionUser joiningUser, Discussion chatRoom);
	Discussion leave(DiscussionUser leavingUser, Discussion chatRoom);
	void sendPublicMessage(InstantMessage instantMessage);
	void sendPrivateMessage(InstantMessage instantMessage);
	List<Discussion> findAll();
}
