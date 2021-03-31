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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
		Discussion discussionRoom = new Discussion();
		Opponent opponent = this.findWorthyOpponent(opponentId1, opponentId2);
		User firstUser = userRepository.findById(opponentId1).get();
		User secondUser = userRepository.findById(opponentId2).get();
		if (null == opponent) {
			System.out.println("Before saving discussRoom In DB: "+discussionRoom.toString());
			discussionRoom = discussionRepository.save(discussionRoom);
			System.out.println("After saving discussRoom In DB: "+discussionRoom.toString());
			this.saveDiscussionUser(firstUser, secondUser, discussionRoom);
			opponent = new Opponent(firstUser.getId(), secondUser.getId(), discussionRoom.getDiscussionId());
			opponentRepository.save(opponent);
		} else {
			try {
				discussionRoom = discussionRepository.findByDiscussionId(opponent.getDiscussionRoomId()).get();
			} catch (Exception ex) {
				discussionRoom = discussionRepository.save(discussionRoom);
				this.saveDiscussionUser(firstUser, secondUser, discussionRoom);
			}
		}
		return discussionRoom;
	}

	private void saveDiscussionUser(User firstUser, User secondUser, Discussion discussRoom) {
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
		discussionRepository.deleteByDiscussionId(id);
		Discussion discussion =null;
		try {
			discussion = discussionRepository.findByDiscussionId(id).get();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public Discussion checkAndGetRoomForUser(Long userId){
		Discussion discussionRoom=null;
		Opponent opponent = opponentRepository.findByOpponentId1OrOpponentId2(userId, userId).orElse(new Opponent());
		if(null != opponent.getId()){
			try {
				discussionRoom = discussionRepository.findByDiscussionId(opponent.getDiscussionRoomId()).get();
			}catch(NoSuchElementException exception){
				System.out.println("No Discussion Room found for this discussionId");
			}
		}
		return  discussionRoom;
	}

}
