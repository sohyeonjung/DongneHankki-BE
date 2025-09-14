package org.netway.dongnehankki.global.config;

import org.netway.dongnehankki.store.application.StoreChunCheonDataService;
import org.netway.dongnehankki.store.application.StoreGwangmyeongDataService;
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

	private final StoreGwangmyeongDataService storeGwangmyeongDataService;
	private final StoreChunCheonDataService storeCheoncheonDataService;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() throws Exception {
		log.info("[서버 시작 완료 후 1회 실행] 매장 데이터 동기화 시작");
		storeGwangmyeongDataService.saveAllStores(1000);
		Thread.sleep(600000);
		storeCheoncheonDataService.saveAllStores(1000);
	}

	@Scheduled(cron = "0 0 0 * * *", zone = TIME_ZONE)
	public void syncGwangMyeongStoreData() {
		log.info("[스케줄러] 광명 매장 데이터 동기화 시작");
		storeGwangmyeongDataService.saveAllStores(1000);
	}

	@Scheduled(cron = "0 30 0 * * *", zone = TIME_ZONE)
	public void syncChunCheonStoreData() throws Exception {
		log.info("[스케줄러] 춘천 매장 데이터 동기화 시작");
		storeCheoncheonDataService.saveAllStores(1000);
	}
}
