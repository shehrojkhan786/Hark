package com.hark.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
	private String message;
	private String status;
	private Object data;

	public MessageResponse(String message){
		this.message=message;
	}

}
