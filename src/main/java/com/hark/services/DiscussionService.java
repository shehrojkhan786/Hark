package com.hark.services;

import java.util.List;

import com.hark.model.Discussion;
import com.hark.model.DiscussionUser;
import com.hark.model.InstantMessage;

public interface DiscussionService {
	
	Discussion save(Discussion discussion);
	Discussion findById(String discussionId);
	//List<Discussion> findByUsername(String username);
	Discussion join(DiscussionUser joiningUser, Discussion discussion);
	Discussion leave(DiscussionUser leavingUser, Discussion discussion);
	void sendPublicMessage(InstantMessage instantMessage);
	void sendPrivateMessage(InstantMessage instantMessage);
	List<Discussion> findAll();
	boolean deleteById(String id);
}
