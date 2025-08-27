package org.netway.dongnehankki;

import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.notification.application.NotificationSchedulingService;
import org.netway.dongnehankki.post.application.VertexAIService;
import org.netway.dongnehankki.store.application.StoreSyncService;
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
	private NotificationSchedulingService notificationSchedulingService;

	@MockitoBean
	private Client vertexClient;

	@MockitoBean
	private VertexAIService vertexAIService;

	@Test
	void contextLoads() {
	}

}
