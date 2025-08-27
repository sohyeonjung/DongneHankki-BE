package org.netway.dongnehankki.notification.application;

import java.util.List;

import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSchedulingService {

	private final FCMNotificationService fcmNotificationService;
	private final UserRepository userRepository;

	@Scheduled(cron = "0 0 8 * * ?") // 매일 아침 8시
	public void scheduleDailyNotificationsForMorning() {
		List<User> owners = userRepository.findByRole(User.Role.OWNER);

		String title = "오늘의 아침 식사를 준비해보세요!";
		String body = "새로운 하루를 상쾌하게 시작할 아침 메뉴를 추천하고 공유해보세요!";

		for (User user : owners) {
			if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
				fcmNotificationService.sendFCMNotifications(user.getFcmToken(), title, body);
			}
		}
		
		log.info("아침 마케팅 알림 전송 완료");
	}

	@Scheduled(cron = "0 0 12 * * ?") // 매일 정오 12시
	public void scheduleDailyNotificationsForLunch() {
		List<User> owners = userRepository.findByRole(User.Role.OWNER);

		String title = "점심 메뉴를 추천해보세요!";
		String body = "점심 시간에 맞는 메뉴 추천 글을 작성해보세요!";

		for(User user : owners) {
			if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
				fcmNotificationService.sendFCMNotifications(user.getFcmToken(), title, body);
			}
		}

		log.info("점심 마케팅 알림 전송 완료");
	}

	@Scheduled(cron = "0 0 18 * * ?") // 매일 저녁 6시
	public void scheduleDailyNotificationsForDinner() {
		List<User> owners = userRepository.findByRole(User.Role.OWNER);

		String title = "저녁 식사를 준비해보세요!";
		String body = "하루의 마무리를 함께할 저녁 메뉴를 추천하고 공유해보세요!";

		for(User user : owners) {
			if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
				fcmNotificationService.sendFCMNotifications(user.getFcmToken(), title, body);
			}
		}

		log.info("저녁 마케팅 알림 전송 완료");
	}

}
