package org.netway.dongnehankki.store.infrastructure.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class StoreOpenApiParserTest {

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private StoreOpenApiParser storeOpenApiParser;

	private StoreOpenApiResponse mockResponse;
	private StoreOpenApiResponse successResponse;
	private StoreOpenApiResponse failResponse;

	@BeforeEach
	void setUp() {
		mockResponse = new StoreOpenApiResponse();
		StoreOpenApiResponse.RegionMnyFacltStus status1 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Head head1 = new StoreOpenApiResponse.Head();
		head1.setListTotalCount(123);
		status1.setHead(Collections.singletonList(head1));

		StoreOpenApiResponse.RegionMnyFacltStus status2 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Row row1 = new StoreOpenApiResponse.Row();
		row1.setBizregno("1234567890");
		row1.setCmpnmNm("Test Store 1");
		row1.setLeadTaxManStateCd("01");
		row1.setRefineWgs84Lat("37.123");
		row1.setRefineWgs84Logt("127.456");
		row1.setRefineRoadnmAddr("Test Road 1");
		row1.setSigunNm("수원시");
		row1.setIndutypeCd("2102");

		StoreOpenApiResponse.Row row2 = new StoreOpenApiResponse.Row();
		row2.setBizregno("0987654321");
		row2.setCmpnmNm("Test Store 2");
		row2.setLeadTaxManStateCd("01");
		row2.setRefineWgs84Lat("37.789");
		row2.setRefineWgs84Logt("127.987");
		row2.setRefineRoadnmAddr("Test Road 2");
		row2.setSigunNm("성남시");
		row2.setIndutypeCd("2201");

		status2.setRow(Arrays.asList(row1, row2));
		mockResponse.setRegionMnyFacltStus(Arrays.asList(status1, status2));


		// isSuccess 테스트를 위한 성공 응답
		successResponse = new StoreOpenApiResponse();
		StoreOpenApiResponse.RegionMnyFacltStus successStatus1 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Head successHead1 = new StoreOpenApiResponse.Head();
		StoreOpenApiResponse.Result successResult = new StoreOpenApiResponse.Result();
		successResult.setCode("INFO-000"); // 성공 코드
		successHead1.setResult(successResult);
		successStatus1.setHead(Arrays.asList(new StoreOpenApiResponse.Head(), successHead1)); // 두 번째 Head에 Result가 있도록 설정
		successResponse.setRegionMnyFacltStus(Arrays.asList(successStatus1, new StoreOpenApiResponse.RegionMnyFacltStus()));


		// isSuccess 테스트를 위한 실패 응답
		failResponse = new StoreOpenApiResponse();
		StoreOpenApiResponse.RegionMnyFacltStus failStatus1 = new StoreOpenApiResponse.RegionMnyFacltStus();
		StoreOpenApiResponse.Head failHead1 = new StoreOpenApiResponse.Head();
		StoreOpenApiResponse.Result failResult = new StoreOpenApiResponse.Result();
		failResult.setCode("ERROR-100");
		failHead1.setResult(failResult);
		failStatus1.setHead(Arrays.asList(new StoreOpenApiResponse.Head(), failHead1));
		failResponse.setRegionMnyFacltStus(Arrays.asList(failStatus1, new StoreOpenApiResponse.RegionMnyFacltStus()));
	}


	@Test
	@DisplayName("parse 메서드 - 유효한 JSON 문자열 파싱 성공")
	void testParse_success() throws IOException {
		// Given
		String json = "{\"key\": \"value\"}";
		when(objectMapper.readValue(anyString(), eq(StoreOpenApiResponse.class))).thenReturn(mockResponse);

		// When
		StoreOpenApiResponse result = storeOpenApiParser.parse(json);

		// Then
		assertNotNull(result);
		assertEquals(mockResponse, result);
		verify(objectMapper, times(1)).readValue(json, StoreOpenApiResponse.class);
	}

	@Test
	@DisplayName("extractTotalCount 메서드 - 총 개수 올바르게 추출")
	void testExtractTotalCount() {
		// Given (setUp에서의 mockResponse)

		// When
		int totalCount = storeOpenApiParser.extractTotalCount(mockResponse);

		// Then
		assertEquals(123, totalCount);
	}


	@Test
	@DisplayName("extractRows 메서드 - 상점 Row 리스트 올바르게 추출")
	void testExtractRows() {
		// Given (setUp에서의 mockResponse)

		// When
		List<StoreOpenApiResponse.Row> rows = storeOpenApiParser.extractRows(mockResponse);

		// Then
		assertNotNull(rows);
		assertEquals(2, rows.size());
		assertEquals("1234567890", rows.get(0).getBizregno());
		assertEquals("Test Store 2", rows.get(1).getCmpnmNm());
	}


	@Test
	@DisplayName("isSuccess 메서드 - 성공 응답 코드일 경우 true 반환")
	void testIsSuccess_success() {
		// Given (setUp에서의 mockResponse)

		// When
		boolean success = storeOpenApiParser.isSuccess(successResponse);

		// Then
		assertTrue(success);
	}

	@Test
	@DisplayName("isSuccess 메서드 - 실패 응답 코드일 경우 false 반환")
	void testIsSuccess_failure() {
		// Given (setUp에서의 failResponse)

		// When
		boolean success = storeOpenApiParser.isSuccess(failResponse);

		// Then
		assertFalse(success);
	}

}
