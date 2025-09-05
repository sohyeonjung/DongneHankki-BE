package org.netway.dongnehankki.global.config;

import org.netway.dongnehankki.store.application.ChunCheonStoreService;
import org.netway.dongnehankki.store.application.StoreSyncService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class ScheduleConfig {

	private static final String TIME_ZONE = "Asia/Seoul";

	private final StoreSyncService storeSyncService;
	private final ChunCheonStoreService chunCheonStoreService;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() throws Exception {
		log.info("[서버 시작 완료 후 1회 실행] 매장 데이터 동기화 시작");
		//storeSyncService.sync();
		chunCheonStoreService.fetchAndSaveAllStores(50);
	}

	// @Scheduled(cron = "0 0 0 * * *", zone = TIME_ZONE)
	// public void syncStoreOpenData() {
	// 	log.info("[스케줄러] 매장 데이터 동기화 시작");
	// 	storeSyncService.sync();
	// }
}
