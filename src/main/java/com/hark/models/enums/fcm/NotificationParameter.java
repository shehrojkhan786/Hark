/**
 * 
 */
package com.hark.models.enums.fcm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum NotificationParameter {
	SOUND("default"), COLOR("#FFFF00");
	
	private String value;
}
