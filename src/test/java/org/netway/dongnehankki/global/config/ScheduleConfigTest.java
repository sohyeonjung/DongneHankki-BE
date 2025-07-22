package org.netway.dongnehankki.global.config;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.application.StoreSyncService;

@ExtendWith(MockitoExtension.class)
class ScheduleConfigTest {

	@Mock
	private StoreSyncService storeSyncService;

	@InjectMocks
	private ScheduleConfig scheduleConfig;

	@Test
	@DisplayName("어플리케이션 실행 시 함수 호출")
	void syncStoreOpenData_shouldCallSyncService() {
		// When
		scheduleConfig.onApplicationReady();

		// Then
		verify(storeSyncService, times(1)).sync();
	}
}

