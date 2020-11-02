/**
 * 
 */
package com.hark.controllers.fcm;

/**
 * @author shkhan
 *
 */
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hark.model.fcm.PushNotificationRequest;
import com.hark.model.fcm.PushNotificationResponse;
import com.hark.services.impl.fcm.PushNotificationService;

@RestController
public class PushNotificationController {
	private PushNotificationService pushNotificationService;

	public PushNotificationController(PushNotificationService pushNotificationService) {
		this.pushNotificationService = pushNotificationService;
	}

	@PostMapping("/notification/topic")
	public ResponseEntity sendNotification(@RequestBody PushNotificationRequest request) {
		pushNotificationService.sendPushNotificationWithoutData(request);
		return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."),
				HttpStatus.OK);
	}

	@PostMapping("/notification/token")
	public ResponseEntity sendTokenNotification(@RequestBody PushNotificationRequest request) {
		pushNotificationService.sendPushNotificationToToken(request);
		return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."),
				HttpStatus.OK);
	}

	@PostMapping("/notification/data")
	public ResponseEntity sendDataNotification(@RequestBody PushNotificationRequest request) {
		pushNotificationService.sendPushNotification(request);
		return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."),
				HttpStatus.OK);
	}
}
