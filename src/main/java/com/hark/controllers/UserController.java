/**
 * 
 */
package com.hark.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hark.model.Badge;
import com.hark.model.Discussion;
import com.hark.model.User;
import com.hark.model.UserRating;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.BadgeRepository;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.RatingRepository;
import com.hark.repositories.UserRepository;
import com.hark.securty.utils.JwtUtils;
import com.hark.services.impl.SearchAndMatchService;

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

	@Autowired
	private BadgeRepository badgeRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private DiscussionRepository discussionRepository;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/searchUserAndCreateRoom")
	public ResponseEntity<?> searchAndMatch(@Valid String username) {
		User user = userRepository.findByUsername(username).get();
		user.setSearching(true);
		User opponent = searchAndMatchService.searchUser(user);
		if (null != opponent) {
			Discussion room = searchAndMatchService.createDiscussionRoom(user.getId(), opponent.getId());
			user.setSearching(false);
			userRepository.save(user);
			return ResponseEntity.ok(room);
		}
		return ResponseEntity.badRequest().body(new MessageResponse("No opponent found, Try again later"));
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getUserDetails(@Valid String username) {
		User user = null;
		try {
			user = userRepository.findByUsername(username).get();
		} catch (NoSuchElementException ex) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is invalid"));
		}

		return ResponseEntity.ok(user);
	}

	@DeleteMapping("/removeRoom")
	public ResponseEntity<?> removeRoom(@Valid String discussionRoomId) {
		boolean isDeleted = searchAndMatchService.deleteDiscussionRoom(discussionRoomId);
		if (isDeleted) {
			return ResponseEntity.ok(new MessageResponse("Room deleted!!!"));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Cannot delete room, try again later!!!"));
	}

	@GetMapping("/badges")
	public ResponseEntity getBadges() {
		List<Badge> badges = badgeRepository.findAll();
		return new ResponseEntity(badges, HttpStatus.OK);
	}

	@PostMapping("/userRating")
	public ResponseEntity<?> userRating(@RequestParam("stars") float stars, @RequestParam("toUser") String toUserName,
			@RequestParam("discussionId") String discussionId) {
		User user = null;
		Discussion discussion = null;
		try {
			user = userRepository.findByUsername(toUserName).get();
		} catch (NoSuchElementException ex) {
			// log here
			return ResponseEntity.badRequest().body(new MessageResponse("Invalid username provided: " + toUserName));
		}

		try {
			discussion = discussionRepository.findById(discussionId).get();
		} catch (NoSuchElementException ex) {
			// log here
			return ResponseEntity.badRequest()
					.body(new MessageResponse("Invalid chat/discusion id provided: " + discussionId));
		}

		UserRating userRating = new UserRating();
		userRating.setStars(stars);
		userRating.setChatRoom(discussion);
		userRating.setUser(user);
		ratingRepository.save(userRating);

		ExampleMatcher userRatingMatcher = ExampleMatcher.matching().withIgnorePaths("id")
				.withMatcher("user_id", GenericPropertyMatchers.exact())
				.withMatcher("discussion_id", GenericPropertyMatchers.exact());
		Example<UserRating> userRatingExample = Example.of(userRating, userRatingMatcher);
		boolean isSaved = ratingRepository.exists(userRatingExample);
		if (isSaved) {
			return ResponseEntity.ok(new MessageResponse("User rating saved!!!"));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Unable to save ratings please, try again!!!"));
	}

}