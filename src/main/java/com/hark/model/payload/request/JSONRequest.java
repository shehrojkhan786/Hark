package com.hark.model.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JSONRequest {
    private String email;
    private String username;
    private String discussionRoomId;
    private float stars;
    private String discussionId;
    private String toUser;
    private Long phone;
    private String country;
    private String deviceId;
    private String politicalParty;
    private String feedback;
    private String userId;
}
