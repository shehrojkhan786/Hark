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
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String chatRoomId = headers.getNativeHeader("chatRoomId").get(0);
		headers.getSessionAttributes().put("chatRoomId", chatRoomId);
		DiscussionUser joiningUser = new DiscussionUser(event.getUser().getName());
		
		chatRoomService.join(joiningUser, chatRoomService.findById(chatRoomId));
	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String chatRoomId = headers.getSessionAttributes().get("chatRoomId").toString();
		DiscussionUser leavingUser = new DiscussionUser(event.getUser().getName());

		chatRoomService.leave(leavingUser, chatRoomService.findById(chatRoomId));
	}
}
