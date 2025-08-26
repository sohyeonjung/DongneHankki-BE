package org.netway.dongnehankki.notification.application;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FCMNotificationService {

	public void sendFCMNotifications(String deviceToken, String title, String body) {
		Notification notification = Notification.builder()
			.setTitle(title)
			.setBody(body)
			.build();
	
		//TODO: key value값 프론트랑 상의해서 필요한 값으로 변경
		Message message = Message.builder()
			.setToken(deviceToken)
			.setNotification(notification)
			.putData("key", "value") 
			.build();

		try {
			String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Successfully sent message to token " + deviceToken + ": " + response);
		} catch (Exception e) {
			System.err.println("Error sending message to token " + deviceToken + ": " + e.getMessage());
		}
	}
}
