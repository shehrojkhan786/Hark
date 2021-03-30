/**
 *
 */
package com.hark.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.request.JSONRequest;
import com.hark.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hark.model.Badge;
import com.hark.model.Discussion;
import com.hark.model.User;
import com.hark.model.UserRating;
import com.hark.model.payload.response.MessageResponse;
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

    final int MAX_COUNTER = 5;

    @PostMapping("/searchUserAndCreateRoom")
    public ResponseEntity<?> searchAndMatch(@Valid @RequestBody JSONRequest request) {
        MessageResponse response = new MessageResponse();
        User user = userRepository.findByEmail(request.getEmail()).get();
        user.setSearching(true);
        userRepository.save(user);
        boolean isOpponentFound = false;
        int searchCounter = 0;
        Discussion room = this.checkAndGetRoomForUser(user.getId());
        if(null == room) {
            while (searchCounter < MAX_COUNTER) {
                User opponent = searchAndMatchService.searchUser(user);
                if (null != opponent) {
                    room = searchAndMatchService.createDiscussionRoom(user.getId(), opponent.getId());
                    isOpponentFound = true;
                }
                if (isOpponentFound) {
                    break;
                } else {
                    searchCounter++;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        System.out.println("Error while searching for user.");
                    }
                }
            }
        }else{
            isOpponentFound=true;
        }
        user.setSearching(false);
        userRepository.save(user);

        if (isOpponentFound) {
            room.setUser(user);
            discussionRepository.save(room);
            response.setData(room);
            response.setMessage("Opponents Found");
            response.setStatus(ResponseStatus.SUCCESS.name());
        } else {
            response.setMessage("No opponent found, Try again later");
            response.setStatus(ResponseStatus.FAILED.name());
        }
        return ResponseEntity.ok().body(response);
    }

    private Discussion checkAndGetRoomForUser(Long userId){
        return searchAndMatchService.checkAndGetRoomForUser(userId);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserDetails(@Valid @RequestBody JSONRequest request) {
        MessageResponse response = new MessageResponse();
        User user = null;
        try {
            user = userRepository.findByEmail(request.getEmail()).get();
        } catch (NoSuchElementException ex) {
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Error: Username is invalid");
            return ResponseEntity.ok(response);
        }
        response.setData(user);
        response.setMessage("Username is valid");
        response.setStatus(ResponseStatus.SUCCESS.name());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/removeRoom")
    public ResponseEntity<?> removeRoom(@Valid @RequestBody String discussionRoomId) {
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
    public ResponseEntity<?> userRating(@RequestParam("stars") float stars, @RequestParam("toUser") String toUserEmail,
                                        @RequestParam("discussionId") String discussionId) {
        User user = null;
        Discussion discussion = null;
        try {
            user = userRepository.findByEmail(toUserEmail).get();
        } catch (NoSuchElementException ex) {
            // log here
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username provided: " + toUserEmail));
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

    @GetMapping("/checkForProfileCompletion")
    public ResponseEntity<?> isProfileCompleted(@Valid @RequestBody String email) {
        boolean isProfileCompleted = false;
        try {
            isProfileCompleted = userRepository.existsByEmailAndIsProfileCompleted(email, true);
        } catch (Exception ex) {
            // log here
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error while checking for profile completion for email: " + email));
        }
        if (isProfileCompleted) {
            return ResponseEntity.ok(new MessageResponse("Profile is completed!!!"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Profile is not compeleted yet. Please complete profile."));
        }
    }

    @PostMapping("/saveProfileDetails")
    public ResponseEntity<?> saveProfileDetails(@Valid @RequestBody Long phone,
                                                @Valid @RequestBody String politicalParty, @Valid @RequestBody String country,
                                                @Valid @RequestBody String email, @Valid @RequestBody String deviceId) {
        User user = null;
        try {
            user = userRepository.findByEmail(email).get();
        } catch (NoSuchElementException ex) {
            // log here
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error while finding user for email: " + email));
        } catch (Exception ex) {
            // log here
            return ResponseEntity.badRequest().body(new MessageResponse("Exception occured: " + ex.getMessage()));
        }

        if (null != user) {
            try {
                user.setCountry(country);
                user.setPoliticalParty(politicalParty);
                user.setPhone(phone);
                user.setDeviceId(deviceId);
                user.setProfileCompleted(true);
                userRepository.save(user);
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(new MessageResponse("Exception occured: " + ex.getMessage()));
            }
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User not found!!!"));
    }

}