package org.netway.dongnehankki;

import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.analytics.application.AnalyticsService;
import org.netway.dongnehankki.notification.application.NotificationSchedulingService;
import org.netway.dongnehankki.post.application.VertexAIService;
import org.netway.dongnehankki.store.application.ChunCheonStoreService;
import org.netway.dongnehankki.store.application.StoreSyncService;
import org.netway.dongnehankki.store.infrastructure.external.AddressApiClient;
import org.netway.dongnehankki.store.infrastructure.external.ChunCheonOpenApiClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.netway.dongnehankki.global.auth.jwt.RefreshTokenRepository;

import com.google.genai.Client;

@SpringBootTest
@ActiveProfiles("test")
class DongneHankkiBeApplicationTests {

	@MockitoBean
	private RefreshTokenRepository refreshTokenRepository;

	@MockitoBean
	private StoreSyncService storeSyncService;

	@MockitoBean
	private ChunCheonStoreService chunCheonStoreService;

	@MockitoBean
	private NotificationSchedulingService notificationSchedulingService;

	@MockitoBean
	private Client vertexClient;

	@MockitoBean
	private AddressApiClient addressApiClient;

	@MockitoBean
	private ChunCheonOpenApiClient chunCheonOpenApiClient;

	@MockitoBean
	private VertexAIService vertexAIService;

	@MockitoBean
	private AnalyticsService analyticsService;

	@Test
	void contextLoads() {
	}

}
