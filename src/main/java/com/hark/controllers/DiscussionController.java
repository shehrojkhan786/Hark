/**
 *
 */
package com.hark.controllers;

import com.hark.model.*;
import com.hark.model.enums.MessageType;
import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.DiscussionFeedbackRepository;
import com.hark.repositories.DiscussionRepository;
import com.hark.repositories.DiscussionUserRepository;
import com.hark.repositories.UserRepository;
import com.hark.services.DiscussionService;
import com.hark.services.InstantMessageService;
import com.hark.services.impl.fcm.PushNotificationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shkhan
 *
 */
@Controller
public class DiscussionController {

    @Autowired
    private DiscussionService chatRoomService;

    @Autowired
    private DiscussionRepository discussionRepository;

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

//    @RequestMapping(path = "/chatroom", method = RequestMethod.POST)
//    @ResponseBody
//    @ResponseStatus(code = HttpStatus.CREATED)
//    public Discussion createChatRoom(@RequestBody Discussion chatRoom) {
//        return chatRoomService.save(chatRoom);
//    }

//    @RequestMapping("/chatroom/{chatRoomId}")
//    public ModelAndView join(@PathVariable String chatRoomId, Principal principal) {
//        ModelAndView modelAndView = new ModelAndView("chatroom");
//        modelAndView.addObject("chatRoom", chatRoomService.findById(chatRoomId));
//        return modelAndView;
//    }

    @SubscribeMapping("/connected.users")
    public MessageResponse listChatRoomConnectedUsersOnSubscribe(SimpMessageHeaderAccessor headerAccessor) {
        MessageResponse response = new MessageResponse();
        String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
        Set<DiscussionUser> discussionUsers = null;
        try {
            discussionUsers = discussionRepository.findByDiscussionId(chatRoomId).get().getDiscussionUsers();;
        }catch(NoSuchElementException exception){
            System.out.println("No discussion room found for discussion Id: "+chatRoomId );
        }
        if(CollectionUtils.isNotEmpty(discussionUsers)) {
            List<User> users = new ArrayList<>(discussionUsers.size());
            for (DiscussionUser discussionUser : discussionUsers) {
                User user = userRepository.findByUsername(discussionUser.getUsername()).get();
                users.add(user);
            }
            response.setStatus(ResponseStatus.SUCCESS.name());
            response.setMessage("Users Found");
            response.setData(users);
        }else{
            response.setStatus(ResponseStatus.FAILED.name());
            response.setMessage("No users Found");
        }
        return response;
    }

    @GetMapping("/api/discussionRoom/connectedUsers")
    @ResponseBody
    public ResponseEntity<?> getConnectedUsersForUser(@RequestParam("id") Long id) {
        MessageResponse response = new MessageResponse();
        try {
            User user = userRepository.findById(id).get();
            if(null != user && CollectionUtils.isNotEmpty(user.getDiscussions())) {
                response.setStatus(com.hark.model.enums.ResponseStatus.SUCCESS.name());
                response.setMessage("Connected Users found");
                user.getDiscussions().forEach(discussion ->
                                    {
                                        Set<DiscussionUser> discussionUsers = discussion.getDiscussionUsers()
                                                .stream()
                                                .filter(discussionUser -> !discussionUser.getUsername().equals(user.getUsername()))
                                                .collect(Collectors.toSet());
                                        discussion.setDiscussionUsers(discussionUsers);
                                    });
                response.setData(user);
            } else {
                response.setStatus(com.hark.model.enums.ResponseStatus.FAILED.name());
                response.setMessage("Connected Users not found for user id: "+id);
            }
        } catch (NoSuchElementException noSuchElementException){
            response.setStatus(com.hark.model.enums.ResponseStatus.ERROR.name());
            response.setMessage("Error while getting  connected users for user id: " + id);
        }
        return ResponseEntity.ok(response);
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
        System.out.println("InstantMessage2 is " + instantMessage);
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
