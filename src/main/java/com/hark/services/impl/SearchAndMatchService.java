/**
 * 
 */
package com.hark.services.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hark.model.Discussion;
import com.hark.model.Opponent;
import com.hark.model.User;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.OpponentRepository;
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
	private DiscussionRepository discussionRepository;

	@Autowired
	private OpponentRepository opponentRepository;

	public User searchUser(User participant) {
		Random rand = new Random();
		User candidate = null;
		List<User> candidates = userRepository.findByIsSearchingAndPoliticalPartyNot(true,
				participant.getPoliticalParty());
		if (null != candidates && candidates.size() > 0) {
			candidate = candidates.get(rand.nextInt(candidates.size()));
		}
		return candidate;
	}

	public Discussion createDiscussionRoom(Long opponentId1, Long opponentId2) {
		Discussion discussRoom = new Discussion();
		Opponent opponent = findWorthyOpponent(opponentId1, opponentId2);
		if (null == opponent) {
			discussRoom = discussionRepository.save(discussRoom);
			opponent = new Opponent(opponentId1, opponentId2, discussRoom.getId());
			opponentRepository.save(opponent);
		} else {
			try {
				discussRoom = discussionRepository.findById(opponent.getDiscussionRoomId()).get();
			} catch (NoSuchElementException ex) {
				discussRoom = discussionRepository.save(discussRoom);
			}
		}
		return discussRoom;
	}

	private Opponent findWorthyOpponent(Long opponentId1, Long opponentId2) {
		Opponent worthyOpponent = null;
		Optional<Opponent> opponent = opponentRepository.findByOpponentId1AndOppoenentId2(opponentId1, opponentId2);
		try {
			worthyOpponent = opponent.get();
		} catch (NoSuchElementException ex) {
			opponent = opponentRepository.findByOpponentId1AndOppoenentId2(opponentId2, opponentId1);
			try {
				worthyOpponent = opponent.get();
			} catch (NoSuchElementException exception) {

			}
		}
		return worthyOpponent;
	}

	public boolean deleteDiscussionRoom(String id) {
		discussionRepository.deleteById(id);
		Optional<Discussion> discussion = discussionRepository.findById(id);
		try {
			discussion.get();
		} catch (NoSuchElementException e) {
			return true;
		}
		return false;
	}

}
