/**
 * 
 */
package com.hark.controllers;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hark.model.Discussion;
import com.hark.model.User;
import com.hark.repositories.UserRepository;

/**
 * @author shkhan
 *
 */
@Service
public class SearchAndMatchService {

	@Autowired
	private UserRepository userRepository;

	public User searchUser(User participant) {
		Random rand = new Random();
		List<User> candidates = userRepository.findByIsSearchingAndPoliticalPartyNot(true,
				participant.getPoliticalParty());
		User candidate = candidates.get(rand.nextInt(candidates.size()));
		return candidate;
	}

	public Discussion createDiscussionRoom() {
		return null;
	}

}
