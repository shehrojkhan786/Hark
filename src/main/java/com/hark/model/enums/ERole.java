package com.hark.model.enums;

public enum ERole {
	USER("User"),
    MODERATOR("Moderator"),
    ADMIN("Admin");

    String role=null;
    ERole(String role){
        this.role=role;
    }
}
