package com.hark.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.hark.model.DiscussionUser;
import com.hark.services.DiscussionService;

@Component
public class WebSocketEvents {

	@Autowired
	private DiscussionService chatRoomService;
	
	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		System.out.println("I am trying to join the session");
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		System.out.println("I am trying to get chatid from the headers");
		System.out.println("My session Id is: "+headers.getSessionId());
		for(String key : headers.getMessageHeaders().keySet()){
			System.out.println("My session headers is: "+String.valueOf(headers.getMessageHeaders().get(key)));
		}
		System.out.println("My session Id is: "+headers.getSessionId());
		String chatRoomId = headers.getMessageHeaders().get("chatRoomId");
		headers.getSessionAttributes().put("chatRoomId", chatRoomId);
		System.out.println("I am putting chatid in the session");
		DiscussionUser joiningUser = new DiscussionUser(event.getUser().getName());		
		chatRoomService.join(joiningUser, chatRoomService.findById(chatRoomId));
		System.out.println("I am able to join the session");
	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		System.out.println("I am trying to leave the session");
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());		
		for(String key : headers.getSessionAttributes().keySet()) {
			System.out.println("My header is "+String.valueOf(headers.getSessionAttributes().get(key).toString()));
		}
		String chatRoomId = headers.getSessionAttributes().get("chatRoomId").toString();
		DiscussionUser leavingUser = new DiscussionUser(event.getUser().getName());
		chatRoomService.leave(leavingUser, chatRoomService.findById(chatRoomId));
		System.out.println("I am able to leave the session");
	}
}
