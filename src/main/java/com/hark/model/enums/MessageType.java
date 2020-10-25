/**
 * 
 */
package com.hark.model.enums;

/**
 * @author shkhan
 *
 */
public enum MessageType {
	TEXT("text"),
    AUDIO("audio"),
    IMAGE("image");

	String messageType = null;
	MessageType(String message) {
		this.messageType = message;
	}
}
