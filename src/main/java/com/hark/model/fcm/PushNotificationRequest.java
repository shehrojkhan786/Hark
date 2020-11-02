/**
 * 
 */
package com.hark.model.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PushNotificationRequest {
    private String title;
    private String message;
    private String topic;
    private String token;
    
    public PushNotificationRequest(Builder builder) {
    	this.title = builder.title;
    	this.message = builder.message;
    	this.topic = builder.topic;
    	this.token = builder.token;
    }
    
    public static Builder builder() {
    	return new Builder();
    }
    
    public static class Builder {
    	private String title;
        private String message;
        private String topic;
        private String token;
        
        private Builder() {
        	
        }

		public String getTitle() {
			return title;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public String getMessage() {
			return message;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public String getTopic() {
			return topic;
		}

		public Builder setTopic(String topic) {
			this.topic = topic;
			return this;
		}

		public String getToken() {
			return token;
		}

		public Builder setToken(String token) {
			this.token = token;
			return this;
		}                

		public PushNotificationRequest build() {
			return new PushNotificationRequest(this);
		}
		
    }    
}