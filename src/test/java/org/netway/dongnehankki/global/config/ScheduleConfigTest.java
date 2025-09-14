package org.netway.dongnehankki.global.config;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.application.StoreChunCheonDataServiceImpl;
import org.netway.dongnehankki.store.application.StoreGwangmyeongDataService;

@ExtendWith(MockitoExtension.class)
class ScheduleConfigTest {

	@Mock
	private StoreGwangmyeongDataService gwangmyeongDataService;

	@Mock
	private StoreChunCheonDataServiceImpl chunCheonStoreService;

	@InjectMocks
	private ScheduleConfig scheduleConfig;

	@Test
	@DisplayName("어플리케이션 실행 시 함수 호출")
	void syncStoreOpenData_shouldCallSyncService() throws Exception {
		// When
		scheduleConfig.onApplicationReady();

		// Then
		verify(gwangmyeongDataService, times(1)).saveAllStores(1000);
		verify(chunCheonStoreService, times(1)).saveAllStores(1000);
	}
}

