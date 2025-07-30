package org.netway.dongnehankki.global.config;

import jakarta.annotation.PostConstruct;
import org.netway.dongnehankki.store.application.StoreSyncService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class ScheduleConfig {

	private static final String TIME_ZONE = "Asia/Seoul";

	private final StoreSyncService storeSyncService;

	@PostConstruct
	public void checkScheduleConfigLoaded() {
		log.info("ScheduleConfig 빈으로 로딩됨");
		log.info("storeSyncService 주입 상태: {}", storeSyncService != null);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("[서버 시작 완료 후 1회 실행] 매장 데이터 동기화 시작");
		storeSyncService.sync();
	}

	@Scheduled(cron = "0 0 0 * * *", zone = TIME_ZONE)
	public void syncStoreOpenData() {
		log.info("[스케줄러] 매장 데이터 동기화 시작");
		storeSyncService.sync();
	}
}
