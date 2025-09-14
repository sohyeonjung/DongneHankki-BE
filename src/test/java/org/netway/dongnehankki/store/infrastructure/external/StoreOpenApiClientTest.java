package org.netway.dongnehankki.store.infrastructure.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.exception.OpenApiException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class) 
class GwangMyeongStoreOpenApiClientTest {

	@Mock 
	private RestClient restClient;

	@InjectMocks
	private GwangMyeongStoreOpenApiClient gwangMyeongStoreOpenApiClient;


	private String apiKey = "testApiKey";
	private String baseUrl = "http://test.api.com/data";
	private String sigunNm = "광명시";


	@BeforeEach
	void setUp() {
		// value 값 주입
		ReflectionTestUtils.setField(gwangMyeongStoreOpenApiClient, "apiKey", apiKey);
		ReflectionTestUtils.setField(gwangMyeongStoreOpenApiClient, "baseUrl", baseUrl);
		ReflectionTestUtils.setField(gwangMyeongStoreOpenApiClient, "sigunNm", sigunNm);
	}

	@DisplayName("fetchStoreData 성공 시 API 호출 및 데이터 반환")
	@Test
	void testFetchStoreData_success() {
		// Given
		int pageIndex = 1;
		int pageSize = 10;
		String expectedResponse = "{\"data\": \"test_store_data\"}";

		RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
		RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

		when(restClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(String.class)).thenReturn(expectedResponse);

		// When
		String actualResponse = gwangMyeongStoreOpenApiClient.fetchStoreData(pageIndex, pageSize);

		// Then
		assertEquals(expectedResponse, actualResponse);
		verify(restClient, times(1)).get();
		verify(requestHeadersUriSpec, times(1)).uri(any(URI.class));
		verify(requestHeadersUriSpec, times(1)).retrieve();
		verify(responseSpec, times(1)).body(String.class);
	}


	@Test
	@DisplayName("fetchStoreData 실패 시 OpenApiException 던짐")
	void testFetchStoreData_exception() {
		// Given
		int pageIndex = 1;
		int pageSize = 10;

		RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
		RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

		when(restClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(String.class)).thenThrow(new RestClientException("API call failed"));

		// When & Then
		assertThrows(OpenApiException.class, () -> {
			gwangMyeongStoreOpenApiClient.fetchStoreData(pageIndex, pageSize);
		});

		verify(restClient, times(1)).get();
		verify(requestHeadersUriSpec, times(1)).uri(any(URI.class));
		verify(requestHeadersUriSpec, times(1)).retrieve();
		verify(responseSpec, times(1)).body(String.class);
	}
}