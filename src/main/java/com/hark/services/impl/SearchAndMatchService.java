/**
 * 
 */
package com.hark.services.impl;

import com.hark.model.Discussion;
import com.hark.model.DiscussionUser;
import com.hark.model.Opponent;
import com.hark.model.User;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.DiscussionUserRepository;
import com.hark.repositories.OpponentRepository;
import com.hark.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

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
	private DiscussionUserRepository discussionUserRepository;

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
		Opponent opponent = this.findWorthyOpponent(opponentId1, opponentId2);
		if (null == opponent) {
			this.saveDiscussionUser(opponentId1, opponentId2, discussRoom);
			System.out.println("Before saving discussRoom In DB: "+discussRoom.toString());
			discussRoom = discussionRepository.save(discussRoom);
			System.out.println("After saving discussRoom In DB: "+discussRoom.toString());
			opponent = new Opponent(opponentId1, opponentId2, discussRoom.getId());
			opponentRepository.save(opponent);
		} else {
			try {
				discussRoom = discussionRepository.findById(opponent.getDiscussionRoomId()).get();
			} catch (NoSuchElementException ex) {
				this.saveDiscussionUser(opponentId1, opponentId2, discussRoom);
				discussRoom = discussionRepository.save(discussRoom);
			}
		}
		return discussRoom;
	}

	private void saveDiscussionUser(Long opponentId1, Long opponentId2, Discussion discussRoom) {
		User firstUser = userRepository.findById(opponentId1).get();
		User secondUser = userRepository.findById(opponentId2).get();

		DiscussionUser discussionUser = new DiscussionUser();
		discussionUser.setUsername(firstUser.getUsername());
		discussionUserRepository.save(discussionUser);
		discussRoom.addUser(discussionUser);

		discussionUser = new DiscussionUser();
		discussionUser.setUsername(secondUser.getUsername());
		discussionUserRepository.save(discussionUser);
		discussRoom.addUser(discussionUser);
	}

	private Opponent findWorthyOpponent(Long opponentId1, Long opponentId2) {
		Opponent worthyOpponent = null;
		Optional<Opponent> opponent = opponentRepository.findByOpponentId1AndOpponentId2(opponentId1, opponentId2);
		try {
			worthyOpponent = opponent.get();
		} catch (NoSuchElementException ex) {
			opponent = opponentRepository.findByOpponentId1AndOpponentId2(opponentId2, opponentId1);
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

	public Discussion checkAndGetRoomForUser(Long userId){
		Discussion discussionRoom=null;
		Opponent opponent = opponentRepository.findByOpponentId1OrOpponentId2(userId, userId).orElse(new Opponent());
		if(null != opponent.getId()){
			discussionRoom = discussionRepository.findById(opponent.getDiscussionRoomId()).get();
		}
		return  discussionRoom;
	}

}
