package com.hark.model.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private Long id;
	private String username;
	private String email;
	private Long phone;
	private String role;
	
	public JwtResponse(String accessToken, Long id, String username, String email, Long phone, String role) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
		this.phone = phone;
	}

}
