package org.netway.dongnehankki.store.application.parser;

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
import org.netway.dongnehankki.store.dto.response.GMStoreOpenApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class GMStoreOpenApiParserTest {

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private GMStoreOpenApiParser gmstoreOpenApiParser;

	private GMStoreOpenApiResponse mockResponse;
	private GMStoreOpenApiResponse successResponse;
	private GMStoreOpenApiResponse failResponse;

	@BeforeEach
	void setUp() {
		mockResponse = new GMStoreOpenApiResponse();
		GMStoreOpenApiResponse.RegionMnyFacltStus status1 = new GMStoreOpenApiResponse.RegionMnyFacltStus();
		GMStoreOpenApiResponse.Head head1 = new GMStoreOpenApiResponse.Head();
		head1.setListTotalCount(123);
		status1.setHead(Collections.singletonList(head1));

		GMStoreOpenApiResponse.RegionMnyFacltStus status2 = new GMStoreOpenApiResponse.RegionMnyFacltStus();
		GMStoreOpenApiResponse.Row row1 = new GMStoreOpenApiResponse.Row();
		row1.setBizregno("1234567890");
		row1.setCmpnmNm("Test Store 1");
		row1.setLeadTaxManStateCd("01");
		row1.setRefineWgs84Lat("37.123");
		row1.setRefineWgs84Logt("127.456");
		row1.setRefineRoadnmAddr("Test Road 1");
		row1.setSigunNm("수원시");
		row1.setIndutypeCd("2102");

		GMStoreOpenApiResponse.Row row2 = new GMStoreOpenApiResponse.Row();
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
		successResponse = new GMStoreOpenApiResponse();
		GMStoreOpenApiResponse.RegionMnyFacltStus successStatus1 = new GMStoreOpenApiResponse.RegionMnyFacltStus();
		GMStoreOpenApiResponse.Head successHead1 = new GMStoreOpenApiResponse.Head();
		GMStoreOpenApiResponse.Result successResult = new GMStoreOpenApiResponse.Result();
		successResult.setCode("INFO-000"); // 성공 코드
		successHead1.setResult(successResult);
		successStatus1.setHead(Arrays.asList(new GMStoreOpenApiResponse.Head(), successHead1)); // 두 번째 Head에 Result가 있도록 설정
		successResponse.setRegionMnyFacltStus(Arrays.asList(successStatus1, new GMStoreOpenApiResponse.RegionMnyFacltStus()));


		// isSuccess 테스트를 위한 실패 응답
		failResponse = new GMStoreOpenApiResponse();
		GMStoreOpenApiResponse.RegionMnyFacltStus failStatus1 = new GMStoreOpenApiResponse.RegionMnyFacltStus();
		GMStoreOpenApiResponse.Head failHead1 = new GMStoreOpenApiResponse.Head();
		GMStoreOpenApiResponse.Result failResult = new GMStoreOpenApiResponse.Result();
		failResult.setCode("ERROR-100");
		failHead1.setResult(failResult);
		failStatus1.setHead(Arrays.asList(new GMStoreOpenApiResponse.Head(), failHead1));
		failResponse.setRegionMnyFacltStus(Arrays.asList(failStatus1, new GMStoreOpenApiResponse.RegionMnyFacltStus()));
	}


	@Test
	@DisplayName("parse 메서드 - 유효한 JSON 문자열 파싱 성공")
	void testParse_success() throws IOException {
		// Given
		String json = "{\"key\": \"value\"}";
		when(objectMapper.readValue(anyString(), eq(GMStoreOpenApiResponse.class))).thenReturn(mockResponse);

		// When
		GMStoreOpenApiResponse result = gmstoreOpenApiParser.parse(json);

		// Then
		assertNotNull(result);
		assertEquals(mockResponse, result);
		verify(objectMapper, times(1)).readValue(json, GMStoreOpenApiResponse.class);
	}

	@Test
	@DisplayName("extractTotalCount 메서드 - 총 개수 올바르게 추출")
	void testExtractTotalCount() {
		// Given (setUp에서의 mockResponse)

		// When
		int totalCount = gmstoreOpenApiParser.extractTotalCount(mockResponse);

		// Then
		assertEquals(123, totalCount);
	}


	@Test
	@DisplayName("extractRows 메서드 - 상점 Row 리스트 올바르게 추출")
	void testExtractRows() {
		// Given (setUp에서의 mockResponse)

		// When
		List<GMStoreOpenApiResponse.Row> rows = gmstoreOpenApiParser.extractRows(mockResponse);

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
		boolean success = gmstoreOpenApiParser.isSuccess(successResponse);

		// Then
		assertTrue(success);
	}

	@Test
	@DisplayName("isSuccess 메서드 - 실패 응답 코드일 경우 false 반환")
	void testIsSuccess_failure() {
		// Given (setUp에서의 failResponse)

		// When
		boolean success = gmstoreOpenApiParser.isSuccess(failResponse);

		// Then
		assertFalse(success);
	}

}
