package com.hark.model;

import java.util.Date;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hark.model.enums.MessageType;
import com.hark.utils.SystemUsers;

import lombok.AllArgsConstructor;
import lombok.Data;


@Table("messages")
@AllArgsConstructor
@Data
public class InstantMessage {
	
	@JsonIgnore
	@PrimaryKeyColumn(name = "username", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String username;
	
	@PrimaryKeyColumn(name = "chatRoomId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String chatRoomId;
	
	@PrimaryKeyColumn(name = "date", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
	private Date date;
	
	private String fromUser;
	private String toUser="All";
	private String text;
	private String messageType;
		
	public InstantMessage() { 
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
		this.messageType = messageType.name();
	}
	
	public MessageType getMessageType() {
		return MessageType.valueOf(this.messageType);
	}
}
