package org.netway.dongnehankki.global.config;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.application.StoreChunCheonDataService;
import org.netway.dongnehankki.store.application.StoreGwangmyeongDataService;

@ExtendWith(MockitoExtension.class)
class ScheduleConfigTest {

	@Mock
	private StoreGwangmyeongDataService gwangmyeongDataService;

	@Mock
	private StoreChunCheonDataService chunCheonStoreService;

	@InjectMocks
	private ScheduleConfig scheduleConfig;

	@Test
	@DisplayName("어플리케이션 시작 시 광명 데이터 동기화 함수 호출")
	void syncGwangmyeongDataOnStartup_shouldCallGwangmyeongDataService() throws Exception {
		// When
		scheduleConfig.syncGwangmyeongDataOnStartup();

		// Then
		verify(gwangmyeongDataService, times(1)).saveAllStores(1000);
		verify(chunCheonStoreService, never()).saveAllStores(anyInt());
	}

	@Test
	@DisplayName("서버 시작 후 10분 지연 춘천 데이터 동기화 함수 호출")
	void syncChunCheonStoreDataOnStartup_shouldCallChunCheonDataService() throws Exception {
		// When
		scheduleConfig.syncChunCheonStoreDataOnStartup();

		// Then
		verify(chunCheonStoreService, times(1)).saveAllStores(1000);
		verify(gwangmyeongDataService, never()).saveAllStores(anyInt());
	}
}

