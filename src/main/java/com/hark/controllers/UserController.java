/**
 * 
 */
package com.hark.controllers;

import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hark.model.Discussion;
import com.hark.model.User;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.UserRepository;
import com.hark.services.SearchAndMatchService;


/**
 * @author shkhan
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user/")
public class UserController {
	
	@Autowired
	private SearchAndMatchService searchAndMatchService;
	

	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/search")
	public ResponseEntity<?> searchAndMatch(@Valid String username){
		User user = userRepository.findByUsername(username).get();
		user.setSearching(true);
		User opponent = searchAndMatchService.searchUser(user);
		if(null != opponent) {
			Discussion room = searchAndMatchService.createDiscussionRoom();
			user.setSearching(false);
			userRepository.save(user);
			return ResponseEntity.ok(room);
		}
		return ResponseEntity.badRequest().body(new MessageResponse("No opponent found, Try again later"));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<?> getUserDetails(@Valid String username){
		User user = null;
		try {
			user = userRepository.findByUsername(username).get();
		}catch(NoSuchElementException ex) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is invalid"));
		}
		
		return ResponseEntity.ok(user);
	}
	
}