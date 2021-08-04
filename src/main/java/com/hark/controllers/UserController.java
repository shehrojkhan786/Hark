/**
 *
 */
package com.hark.controllers;

import com.hark.model.Badge;
import com.hark.model.Discussion;
import com.hark.model.User;
import com.hark.model.UserRating;
import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.request.JSONRequest;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.BadgeRepository;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.RatingRepository;
import com.hark.repositories.UserRepository;
import com.hark.securty.utils.JwtUtils;
import com.hark.services.impl.SearchAndMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

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
            user.getDiscussions().add(room);
            userRepository.save(user);
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

    @GetMapping("/getUserDetails")
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
    public ResponseEntity<?> userRating(@Valid @RequestBody JSONRequest request, Principal principal) {
        User fromUser = null;
        User toUser = null;
        Discussion discussion = null;
        MessageResponse response = new MessageResponse();
        System.out.print("here are my stars: "+request.getStars());
        try {
            toUser = userRepository.findByEmail(request.getToUser()).get();
        } catch (NoSuchElementException ex) {
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Invalid username provided: " + request.getToUser());
            return ResponseEntity.ok(response);
        }

        try{
            fromUser = userRepository.findByUsername(principal.getName()).get();
        }catch (NoSuchElementException ex) {
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Invalid logged in username provided: " + principal.getName());
            return ResponseEntity.ok(response);
        }

        try {
            discussion = discussionRepository.findByDiscussionId(request.getDiscussionId()).get();
        } catch (NoSuchElementException ex) {
            // log here
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Invalid chat/discussion id provided: " + request.getDiscussionId());
            return ResponseEntity.ok(response);
        }

        UserRating userRating = new UserRating();
        userRating.setStars(request.getStars());
        userRating.setChatRoom(discussion);
        userRating.setToUser(toUser);
        userRating.setFromUser(fromUser);
        ratingRepository.save(userRating);

        ExampleMatcher userRatingMatcher = ExampleMatcher.matching().withIgnorePaths("id")
                .withMatcher("to_user_id", GenericPropertyMatchers.exact())
                .withMatcher("from_user_id", GenericPropertyMatchers.exact())
                .withMatcher("discussion_id", GenericPropertyMatchers.exact());
        Example<UserRating> userRatingExample = Example.of(userRating, userRatingMatcher);
        boolean isSaved = ratingRepository.exists(userRatingExample);
        if (isSaved) {
            response.setStatus(ResponseStatus.SUCCESS.name());
            response.setMessage("User rating saved!!!");
        }else {
            response.setStatus(ResponseStatus.FAILED.name());
            response.setMessage("Unable to save ratings please, try again!!!");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkForProfileCompletion")
    public ResponseEntity<?> isProfileCompleted(@Valid @RequestBody JSONRequest request) {
        boolean isProfileCompleted = false;
        MessageResponse response = new MessageResponse();
        try {
            isProfileCompleted = userRepository.existsByEmailAndIsProfileCompleted(request.getEmail(), true);
        } catch (Exception ex) {
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Error while checking for profile completion for email: " + request.getEmail());
        }
        if (isProfileCompleted) {
            response.setStatus(ResponseStatus.SUCCESS.name());
            response.setMessage("Profile is completed!!!");
        } else {
            response.setStatus(ResponseStatus.FAILED.name());
            response.setMessage("Profile is not completed yet. Please complete profile.");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/saveProfileDetails")
    public ResponseEntity<?> saveProfileDetails(@Valid @RequestBody JSONRequest request) {
        User user = null;
        MessageResponse response = new MessageResponse();
        try {
            user = userRepository.findByEmail(request.getEmail()).get();
        } catch (NoSuchElementException ex) {
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Error while finding user for email: " + request.getEmail());
        } catch (Exception ex) {
            response.setStatus(ResponseStatus.ERROR.name());
            response.setMessage("Error for email: " + request.getEmail());
        }

        if (null != user) {
            try {
                user.setCountry(request.getCountry());
                user.setPoliticalParty(request.getPoliticalParty());
                user.setPhone(request.getPhone());
                user.setProfileCompleted(true);
                userRepository.save(user);
                response.setStatus(ResponseStatus.SUCCESS.name());
                response.setMessage("Saved user data for email: " + request.getEmail());
            } catch (Exception ex) {
                response.setStatus(ResponseStatus.ERROR.name());
                response.setMessage("Error while saving user data for email: " + request.getEmail());
            }
        }else {
            response.setStatus(ResponseStatus.FAILED.name());
            response.setMessage("No user found for email: " + request.getEmail());
        }
        return ResponseEntity.ok(response);
    }
}