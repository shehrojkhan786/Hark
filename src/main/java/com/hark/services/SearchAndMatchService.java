/**
 * 
 */
package com.hark.services;

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
	
	@Autowired
	private DiscussionService discussionService;

	public User searchUser(User participant) {
		Random rand = new Random();
		User candidate = null;
		List<User> candidates = userRepository.findByIsSearchingAndPoliticalPartyNot(true,participant.getPoliticalParty());
		if(null != candidates && candidates.size() > 0) {
			candidate = candidates.get(rand.nextInt(candidates.size()));
		}
		return candidate;
	}

	public Discussion createDiscussionRoom() {
		Discussion discussRoom = new Discussion();
		discussRoom = discussionService.save(discussRoom);
		return discussRoom;
	}

}
