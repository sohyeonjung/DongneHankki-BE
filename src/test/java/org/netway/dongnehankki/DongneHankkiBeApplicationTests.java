package org.netway.dongnehankki;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.netway.dongnehankki.global.auth.jwt.RefreshTokenRepository;

@SpringBootTest
@ActiveProfiles("test")
class DongneHankkiBeApplicationTests {

	@MockitoBean
	private RefreshTokenRepository refreshTokenRepository;

	@Test
	void contextLoads() {
	}

}
