/**
 * 
 */
package com.hark.model.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationResponse {

	private int status;
	private String message;

}
