package com.hark.model;

import java.util.Date;

import com.hark.model.enums.MessageType;
import com.hark.utils.SystemUsers;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class InstantMessage2 {
	
	private Date date;
	
	private String fromUser;
	private String toUser="All";
	private String text;
	private String chatMessageType;
		
	public InstantMessage2() { 
		this.date = new Date();
	}
	
	public boolean isPublic() {
		return isNullOrEmpty(this.toUser);
	}
	public boolean isFromAdmin() {
		return this.fromUser.equals(SystemUsers.ADMIN.getUsername());
	}
	
	public boolean isNullOrEmpty(String text) {
		return  null == text ? true : text.isBlank();
	}
	
	public void setMessageType(MessageType messageType) {
		this.chatMessageType = messageType.name();
	}
	
	public MessageType getMessageType() {
		return MessageType.valueOf(this.chatMessageType);
	}
}
