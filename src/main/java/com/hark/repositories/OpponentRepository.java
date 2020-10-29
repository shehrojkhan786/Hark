/**
 * 
 */
package com.hark.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hark.model.Opponent;

/**
 * @author shkhan
 *
 */
@Repository
public interface OpponentRepository extends JpaRepository<Opponent, Long> {	
	Optional<Opponent> findByOpponentId1AndOppoenentId2(Long opponentId1,Long opponentId2);
	void deleteByDiscussionRoomId(String discussionRoomId);
}
