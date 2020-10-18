/**
 * 
 */
package com.hark.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hark.model.User;

/**
 * @author shkhan
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
		
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
	
	Boolean existsByPhone(Long phone);
	
	List<User> findByIsSearchingAndPoliticalPartyNot(Boolean isSearching,String politicalParty);

}
