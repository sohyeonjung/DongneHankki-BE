package org.netway.dongnehankki.store.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;
import org.netway.dongnehankki.store.exception.OpenApiException;
import org.netway.dongnehankki.store.infrastructure.external.StoreOpenApiClient;
import org.netway.dongnehankki.store.infrastructure.external.StoreOpenApiParser;

@ExtendWith(MockitoExtension.class)
class StoreSyncServiceTest {

	@Mock
	private StoreOpenApiClient storeOpenApiClient;

	@Mock
	private StoreOpenApiParser storeOpenApiParser;

	@Mock
	private StoreDataService storeDataService;

	@InjectMocks
	private StoreSyncService storeSyncService;

	private static final int PAGE_SIZE = 1000;
	private StoreOpenApiResponse mockSuccessResponse;
	private StoreOpenApiResponse mockFailureResponse;
	private List<StoreOpenApiResponse.Row> mockRows;

	@BeforeEach
	void setUp() {
		mockSuccessResponse = new StoreOpenApiResponse();
		StoreOpenApiResponse.RegionMnyFacltStus status1 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Head head1 = new StoreOpenApiResponse.Head();
		head1.setListTotalCount(2500);
		StoreOpenApiResponse.Result resultSuccess = new StoreOpenApiResponse.Result();
		resultSuccess.setCode("INFO-000");
		head1.setResult(resultSuccess);
		status1.setHead(Arrays.asList(new StoreOpenApiResponse.Head(), head1));

		StoreOpenApiResponse.RegionMnyFacltStus status2 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Row row1 = new StoreOpenApiResponse.Row(); row1.setBizregno("111");
		StoreOpenApiResponse.Row row2 = new StoreOpenApiResponse.Row(); row2.setBizregno("222");
		mockRows = Arrays.asList(row1, row2);
		status2.setRow(mockRows);

		mockSuccessResponse.setRegionMnyFacltStus(Arrays.asList(status1, status2));


		mockFailureResponse = new StoreOpenApiResponse();
		StoreOpenApiResponse.RegionMnyFacltStus failStatus1 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Head failHead1 = new StoreOpenApiResponse.Head();
		StoreOpenApiResponse.Result resultFail = new StoreOpenApiResponse.Result();
		resultFail.setCode("ERROR-999");
		failHead1.setResult(resultFail);
		failStatus1.setHead(Arrays.asList(new StoreOpenApiResponse.Head(), failHead1));
		mockFailureResponse.setRegionMnyFacltStus(Arrays.asList(failStatus1, new StoreOpenApiResponse.RegionMnyFacltStus()));
	}


	@Test
	@DisplayName("sync - 모든 페이지 데이터 성공적으로 동기화")
	void testSync_success() throws IOException {
		// Given
		when(storeOpenApiClient.fetchStoreData(1, PAGE_SIZE)).thenReturn("first page json");
		when(storeOpenApiParser.parse("first page json")).thenReturn(mockSuccessResponse);
		when(storeOpenApiParser.isSuccess(mockSuccessResponse)).thenReturn(true);
		when(storeOpenApiParser.extractTotalCount(mockSuccessResponse)).thenReturn(2500);
		when(storeOpenApiParser.extractRows(mockSuccessResponse)).thenReturn(mockRows);

		when(storeOpenApiClient.fetchStoreData(2, PAGE_SIZE)).thenReturn("second page json");
		when(storeOpenApiParser.parse("second page json")).thenReturn(mockSuccessResponse);
		when(storeOpenApiParser.isSuccess(mockSuccessResponse)).thenReturn(true);
		when(storeOpenApiParser.extractRows(mockSuccessResponse)).thenReturn(mockRows);

		when(storeOpenApiClient.fetchStoreData(3, PAGE_SIZE)).thenReturn("third page json");
		when(storeOpenApiParser.parse("third page json")).thenReturn(mockSuccessResponse);
		when(storeOpenApiParser.isSuccess(mockSuccessResponse)).thenReturn(true);
		when(storeOpenApiParser.extractRows(mockSuccessResponse)).thenReturn(mockRows);

		// When
		storeSyncService.sync();

		// Then
		verify(storeOpenApiClient, times(1)).fetchStoreData(1, PAGE_SIZE);
		verify(storeOpenApiClient, times(1)).fetchStoreData(2, PAGE_SIZE);
		verify(storeOpenApiClient, times(1)).fetchStoreData(3, PAGE_SIZE);

		verify(storeOpenApiParser, times(1)).parse("first page json");
		verify(storeOpenApiParser, times(1)).extractTotalCount(mockSuccessResponse);
		verify(storeOpenApiParser, times(3)).isSuccess(mockSuccessResponse);
		verify(storeOpenApiParser, times(3)).extractRows(mockSuccessResponse);
		verify(storeDataService, times(3)).processAndSaveStores(mockRows);
	}

	@Test
	@DisplayName("sync - 첫 번째 API 호출 실패 시 OpenApiException 발생")
	void testSync_firstApiCallFails() throws IOException {
		// Given
		when(storeOpenApiClient.fetchStoreData(1, PAGE_SIZE)).thenThrow(new OpenApiException());

		// When & Then
		assertThrows(OpenApiException.class, () -> storeSyncService.sync());

		verify(storeOpenApiParser, never()).parse(anyString());
		verifyNoInteractions(storeDataService);
	}


	@Test
	@DisplayName("sync - 첫 번째 파싱 실패 시 OpenApiException 발생")
	void testSync_firstParseFails() throws IOException {
		// Given
		when(storeOpenApiClient.fetchStoreData(1, PAGE_SIZE)).thenReturn("invalid json");
		when(storeOpenApiParser.parse(anyString())).thenThrow(new IOException("Parsing failed"));

		// When & Then
		assertThrows(OpenApiException.class, () -> storeSyncService.sync());

		// storeDataService는 호출되지 않았는지 검증
		verifyNoInteractions(storeDataService);
		verify(storeOpenApiParser, never()).isSuccess(any(StoreOpenApiResponse.class));
	}


	@Test
	@DisplayName("sync - 첫 번째 API 응답 실패(isSuccess=false) 시 OpenApiException 발생")
	void testSync_firstResponseNotSuccess() throws IOException {
		// Given
		when(storeOpenApiClient.fetchStoreData(1, PAGE_SIZE)).thenReturn("some json");
		when(storeOpenApiParser.parse(anyString())).thenReturn(mockFailureResponse);
		when(storeOpenApiParser.isSuccess(mockFailureResponse)).thenReturn(false);

		// When & Then
		assertThrows(OpenApiException.class, () -> storeSyncService.sync());

		verify(storeOpenApiParser, never()).extractTotalCount(any());
		verify(storeOpenApiParser, never()).extractRows(any());
		verifyNoInteractions(storeDataService);
	}


	@Test
	@DisplayName("sync - 중간 페이지 API 응답 실패(isSuccess=false) 시 해당 페이지 건너뛰고 진행")
	void testSync_middlePageNotSuccessSkips() throws IOException {
		// Given
		when(storeOpenApiClient.fetchStoreData(1, PAGE_SIZE)).thenReturn("first page json");
		when(storeOpenApiParser.parse("first page json")).thenReturn(mockSuccessResponse);
		when(storeOpenApiParser.isSuccess(mockSuccessResponse)).thenReturn(true);
		when(storeOpenApiParser.extractTotalCount(mockSuccessResponse)).thenReturn(2500);
		when(storeOpenApiParser.extractRows(mockSuccessResponse)).thenReturn(mockRows);

		when(storeOpenApiClient.fetchStoreData(2, PAGE_SIZE)).thenReturn("second page json");
		when(storeOpenApiParser.parse("second page json")).thenReturn(mockFailureResponse);
		when(storeOpenApiParser.isSuccess(mockFailureResponse)).thenReturn(false);

		when(storeOpenApiClient.fetchStoreData(3, PAGE_SIZE)).thenReturn("third page json");
		when(storeOpenApiParser.parse("third page json")).thenReturn(mockSuccessResponse);
		when(storeOpenApiParser.isSuccess(mockSuccessResponse)).thenReturn(true);
		when(storeOpenApiParser.extractRows(mockSuccessResponse)).thenReturn(mockRows);

		// When
		storeSyncService.sync();

		// Then
		verify(storeOpenApiClient, times(1)).fetchStoreData(1, PAGE_SIZE);
		verify(storeOpenApiClient, times(1)).fetchStoreData(2, PAGE_SIZE);
		verify(storeOpenApiClient, times(1)).fetchStoreData(3, PAGE_SIZE);

		verify(storeOpenApiParser, times(2)).isSuccess(mockSuccessResponse);
		verify(storeOpenApiParser, times(1)).isSuccess(mockFailureResponse);

		verify(storeDataService, times(2)).processAndSaveStores(mockRows);

		verify(storeOpenApiParser, times(2)).extractRows(mockSuccessResponse);
		verify(storeOpenApiParser, never()).extractRows(mockFailureResponse);
	}


	@Test
	@DisplayName("sync - 내부에서 예상치 못한 Exception 발생 시 OpenApiException으로 랩핑하여 던짐")
	void testSync_unexpectedExceptionWrappedAsOpenApiException() throws IOException {
		// Given

		when(storeOpenApiClient.fetchStoreData(1, PAGE_SIZE)).thenReturn("json");
		when(storeOpenApiParser.parse(anyString())).thenThrow(new RuntimeException("Unexpected error during parsing")); // RuntimeException 발생

		// When & Then
		assertThrows(OpenApiException.class, () -> storeSyncService.sync());
	}
}

