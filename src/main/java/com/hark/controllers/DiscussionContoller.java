/**
 * 
 */
package com.hark.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.hark.model.Discussion;
import com.hark.model.DiscussionFeedback;
import com.hark.model.DiscussionUser;
import com.hark.model.InstantMessage;
import com.hark.model.User;
import com.hark.model.enums.MessageType;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.DiscussionFeedbackRepository;
import com.hark.repositories.DiscussionUserRepository;
import com.hark.repositories.UserRepository;
import com.hark.services.DiscussionService;
import com.hark.services.InstantMessageService;
import com.hark.services.impl.fcm.PushNotificationService;

/**
 * @author shkhan
 *
 */
@Controller
public class DiscussionContoller {

	@Autowired
	private DiscussionService chatRoomService;

	@Autowired
	private DiscussionUserRepository discussionUserRepository;

	@Autowired
	private DiscussionFeedbackRepository discussionFeedbackRepository;

	@Autowired
	private InstantMessageService instantMessageService;

	@Autowired
	private PushNotificationService pushNotificationService;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(path = "/chatroom", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(code = HttpStatus.CREATED)
	public Discussion createChatRoom(@RequestBody Discussion chatRoom) {
		return chatRoomService.save(chatRoom);
	}

	@RequestMapping("/chatroom/{chatRoomId}")
	public ModelAndView join(@PathVariable String chatRoomId, Principal principal) {
		ModelAndView modelAndView = new ModelAndView("chatroom");
		modelAndView.addObject("chatRoom", chatRoomService.findById(chatRoomId));
		return modelAndView;
	}

	@SubscribeMapping("/connected.users")
	public List<DiscussionUser> listChatRoomConnectedUsersOnSubscribe(SimpMessageHeaderAccessor headerAccessor) {
		String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
		return new ArrayList<DiscussionUser>(chatRoomService.findById(chatRoomId).getUsers());
	}

	@GetMapping("/discussionRoom/connectedUsers")
	@ResponseBody
	public ResponseEntity<?> getConnectedUsersForUser(@RequestParam("username") String username) {
		List<Discussion> userDiscussions = null;
		try{
			userDiscussions = chatRoomService.findByUsername(username);
		}catch(Exception exception){
			return ResponseEntity.badRequest()
					.body(new MessageResponse("No discussions found for user: " + username));
		}
		return ResponseEntity.ok(userDiscussions);
	}

	@SubscribeMapping("/old.messages")
	public List<InstantMessage> listOldMessagesFromUserOnSubscribe(Principal principal,
			SimpMessageHeaderAccessor headerAccessor) {
		String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
		return instantMessageService.findAllInstantMessagesFor(principal.getName(), chatRoomId);
	}

	@MessageMapping("/send.message")
	public void sendMessage(@Payload InstantMessage instantMessage, Principal principal,
			SimpMessageHeaderAccessor headerAccessor) {
		String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
		System.out.println("InstantMessage2 is "+instantMessage);
		instantMessage.setFromUser(principal.getName());
		instantMessage.setChatRoomId(chatRoomId);
		instantMessage.setMessageType(MessageType.valueOf(instantMessage.getChatMessageType()));

		if (instantMessage.isPublic()) {
			chatRoomService.sendPublicMessage(instantMessage);
		} else {
			chatRoomService.sendPrivateMessage(instantMessage);
		}

		User toUser = null;
		try {
			toUser = userRepository.findByUsername(instantMessage.getToUser()).get();
		} catch (NoSuchElementException ex) {
			// do nothing
			System.out.println(ex.getMessage());
		}

//		if (null != toUser) {
//			PushNotificationRequest request = PushNotificationRequest.builder().setMessage(instantMessage.getText())
//					.setTitle("From User: " + instantMessage.getFromUser()).setToken(toUser.getDeviceId()).build();
//			pushNotificationService.sendPushNotificationWithoutData(request);
//		}
	}

	@PostMapping("/discussionFeedback")
	@ResponseBody
	public ResponseEntity<?> discussionFeedback(@RequestParam("feedback") String comment,
			@RequestParam("toUser") String toUserName) {

		DiscussionUser discussionUser = new DiscussionUser();
		discussionUser.setUsername(toUserName);
		discussionUserRepository.save(discussionUser);

		DiscussionFeedback discussionFeedback = new DiscussionFeedback();
		discussionFeedback.setComment(comment);
		discussionFeedback.setDiscussionUser(discussionUser);
		discussionFeedbackRepository.save(discussionFeedback);

		ExampleMatcher discussionFeedbackMatcher = ExampleMatcher.matching().withIgnorePaths("id")
				.withMatcher("discussion_user_id", GenericPropertyMatchers.exact());
		Example<DiscussionFeedback> userRatingExample = Example.of(discussionFeedback, discussionFeedbackMatcher);
		boolean isSaved = discussionFeedbackRepository.exists(userRatingExample);
		if (isSaved) {
			return ResponseEntity.ok(new MessageResponse("Discussion feedback saved!!!"));
		}
		return ResponseEntity.badRequest()
				.body(new MessageResponse("Unable to save discussion feedback please, try again!!!"));
	}
}
