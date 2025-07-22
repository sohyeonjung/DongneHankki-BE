package org.netway.dongnehankki.store.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StoreDataServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private StoreDataService storeDataService;

	private StoreOpenApiResponse.Row validRow;
	private StoreOpenApiResponse.Row invalidNullFieldRow;
	private StoreOpenApiResponse.Row invalidClosedRow;
	private StoreOpenApiResponse.Row invalidInduTypeRow;
	private Store existingStore;

	@BeforeEach
	void setUp() {
		validRow = new StoreOpenApiResponse.Row();
		validRow.setLeadTaxManStateCd("01");
		validRow.setCmpnmNm("Valid Store");
		validRow.setRefineWgs84Lat("37.123");
		validRow.setRefineWgs84Logt("127.456");
		validRow.setRefineRoadnmAddr("Valid Road Address");
		validRow.setSigunNm("수원시");
		validRow.setIndutypeCd("2102");
		validRow.setBizregno("1234567890");


		invalidNullFieldRow = new StoreOpenApiResponse.Row();
		invalidNullFieldRow.setLeadTaxManStateCd("01");
		invalidNullFieldRow.setCmpnmNm(null);
		invalidNullFieldRow.setRefineWgs84Lat("37.123");
		invalidNullFieldRow.setRefineWgs84Logt("127.456");
		invalidNullFieldRow.setRefineRoadnmAddr("Valid Road Address");
		invalidNullFieldRow.setSigunNm("수원시");
		invalidNullFieldRow.setIndutypeCd("2102");
		invalidNullFieldRow.setBizregno("0000000001");


		invalidClosedRow = new StoreOpenApiResponse.Row();
		invalidClosedRow.setLeadTaxManStateCd("02");
		invalidClosedRow.setCmpnmNm("Closed Store");
		invalidClosedRow.setRefineWgs84Lat("37.123");
		invalidClosedRow.setRefineWgs84Logt("127.456");
		invalidClosedRow.setRefineRoadnmAddr("Closed Road Address");
		invalidClosedRow.setSigunNm("수원시");
		invalidClosedRow.setIndutypeCd("2102");
		invalidClosedRow.setBizregno("0000000002");


		invalidInduTypeRow = new StoreOpenApiResponse.Row();
		invalidInduTypeRow.setLeadTaxManStateCd("01");
		invalidInduTypeRow.setCmpnmNm("Invalid InduType Store");
		invalidInduTypeRow.setRefineWgs84Lat("37.123");
		invalidInduTypeRow.setRefineWgs84Logt("127.456");
		invalidInduTypeRow.setRefineRoadnmAddr("Invalid InduType Road Address");
		invalidInduTypeRow.setSigunNm("수원시");
		invalidInduTypeRow.setIndutypeCd("9999");
		invalidInduTypeRow.setBizregno("0000000003");

		existingStore = Store.createStore(
			"Existing Store", 37.0, 127.0,
			"Old Address", "Old Sigun", "2102", "1234567890" // validRow와 동일한 사업자등록번호
		);
	}


	@Test
	@DisplayName("processAndSaveStores - null 리스트 처리 시 아무 작업도 수행 안함")
	void testProcessAndSaveStores_nullList() {
		// When
		storeDataService.processAndSaveStores(null);

		// Then
		verifyNoInteractions(storeRepository);
	}

	@Test
	@DisplayName("processAndSaveStores - 빈 리스트 처리 시 아무 작업도 수행 안함")
	void testProcessAndSaveStores_emptyList() {
		// When
		storeDataService.processAndSaveStores(Collections.emptyList());

		// Then
		verifyNoInteractions(storeRepository);
	}

	@Test
	@DisplayName("processAndSaveStores - 새로운 Store 저장 성공")
	void testProcessAndSaveStores_addNewStore() {
		// Given
		when(storeRepository.findByBusinessRegistrationNumber(validRow.getBizregno())).thenReturn(Optional.empty());

		// When
		storeDataService.processAndSaveStores(Collections.singletonList(validRow));

		// Then
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(validRow.getBizregno());
		verify(storeRepository, times(1)).save(any(Store.class));
	}

	@Test
	@DisplayName("processAndSaveStores - 기존 Store 업데이트 성공")
	void testProcessAndSaveStores_updateExistingStore() {
		// Given
		when(storeRepository.findByBusinessRegistrationNumber(validRow.getBizregno())).thenReturn(Optional.of(existingStore));

		// When
		storeDataService.processAndSaveStores(Collections.singletonList(validRow));

		// Then
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(validRow.getBizregno());
		verify(storeRepository, times(1)).save(any(Store.class));

		assertEquals(validRow.getCmpnmNm(), existingStore.getName());
		assertEquals(Double.parseDouble(validRow.getRefineWgs84Lat()), existingStore.getLatitude());
	}


	@Test
	@DisplayName("processAndSaveStores - 필수 필드 누락 Row 건너뛰기")
	void testProcessAndSaveStores_skipNullFieldRow() {
		// Given
		List<StoreOpenApiResponse.Row> rows = Arrays.asList(invalidNullFieldRow, validRow);

		when(storeRepository.findByBusinessRegistrationNumber(validRow.getBizregno())).thenReturn(Optional.empty());
		when(storeRepository.save(any(Store.class))).thenReturn(any(Store.class));

		// When
		storeDataService.processAndSaveStores(rows);

		// Then
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(validRow.getBizregno());
		verify(storeRepository, times(1)).save(any(Store.class));
		verify(storeRepository, never()).findByBusinessRegistrationNumber(invalidNullFieldRow.getBizregno());
	}

	@Test
	@DisplayName("processAndSaveStores - 휴폐업 상태 Row 건너뛰기")
	void testProcessAndSaveStores_skipClosedRow() {
		// Given
		List<StoreOpenApiResponse.Row> rows = Arrays.asList(invalidClosedRow, validRow);

		when(storeRepository.findByBusinessRegistrationNumber(validRow.getBizregno())).thenReturn(Optional.empty());
		when(storeRepository.save(any(Store.class))).thenReturn(any(Store.class));

		// When
		storeDataService.processAndSaveStores(rows);

		// Then
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(validRow.getBizregno());
		verify(storeRepository, times(1)).save(any(Store.class));
		verify(storeRepository, never()).findByBusinessRegistrationNumber(invalidClosedRow.getBizregno());
	}

	@Test
	@DisplayName("processAndSaveStores - 유효하지 않은 업종 코드 Row 건너뛰기")
	void testProcessAndSaveStores_skipInvalidInduTypeRow() {
		// Given
		List<StoreOpenApiResponse.Row> rows = Arrays.asList(invalidInduTypeRow, validRow);

		when(storeRepository.findByBusinessRegistrationNumber(validRow.getBizregno())).thenReturn(Optional.empty());
		when(storeRepository.save(any(Store.class))).thenReturn(any(Store.class));

		// When
		storeDataService.processAndSaveStores(rows);

		// Then
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(validRow.getBizregno());
		verify(storeRepository, times(1)).save(any(Store.class));
		verify(storeRepository, never()).findByBusinessRegistrationNumber(invalidInduTypeRow.getBizregno());
	}


	@Test
	@DisplayName("processAndSaveStores - 여러 Row 데이터 처리 (신규 및 업데이트)")
	void testProcessAndSaveStores_multipleRows() {
		// Given
		StoreOpenApiResponse.Row newStoreRow = new StoreOpenApiResponse.Row();
		newStoreRow.setLeadTaxManStateCd("01");
		newStoreRow.setCmpnmNm("New Store 1");
		newStoreRow.setRefineWgs84Lat("38.0");
		newStoreRow.setRefineWgs84Logt("128.0");
		newStoreRow.setRefineRoadnmAddr("New Address");
		newStoreRow.setSigunNm("용인시");
		newStoreRow.setIndutypeCd("2301");
		newStoreRow.setBizregno("1111111111");

		StoreOpenApiResponse.Row updatedStoreRow = new StoreOpenApiResponse.Row();
		updatedStoreRow.setLeadTaxManStateCd("01");
		updatedStoreRow.setCmpnmNm("Updated Existing Store");
		updatedStoreRow.setRefineWgs84Lat("37.1");
		updatedStoreRow.setRefineWgs84Logt("127.1");
		updatedStoreRow.setRefineRoadnmAddr("Updated Address");
		updatedStoreRow.setSigunNm("수원시");
		updatedStoreRow.setIndutypeCd("2102");
		updatedStoreRow.setBizregno("1234567890");

		List<StoreOpenApiResponse.Row> apiRows = Arrays.asList(newStoreRow, updatedStoreRow, invalidClosedRow);

		when(storeRepository.findByBusinessRegistrationNumber(newStoreRow.getBizregno())).thenReturn(Optional.empty());
		when(storeRepository.findByBusinessRegistrationNumber(updatedStoreRow.getBizregno())).thenReturn(Optional.of(existingStore));

		// When
		storeDataService.processAndSaveStores(apiRows);

		// Then
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(newStoreRow.getBizregno());
		verify(storeRepository, times(1)).findByBusinessRegistrationNumber(updatedStoreRow.getBizregno());
		verify(storeRepository, never()).findByBusinessRegistrationNumber(invalidClosedRow.getBizregno()); // 건너뛴 Row는 호출되면 안됨

		verify(storeRepository, times(2)).save(any(Store.class));

		assertEquals(updatedStoreRow.getCmpnmNm(), existingStore.getName());
		assertEquals(Double.parseDouble(updatedStoreRow.getRefineWgs84Lat()), existingStore.getLatitude());
	}


	@Test
	@DisplayName("parseToDouble - 유효한 문자열 파싱 성공")
	void testParseToDouble_validString() throws Exception {
		Double result = (Double) ReflectionTestUtils.invokeMethod(storeDataService, "parseToDouble", "123.45");
		assertEquals(123.45, result, 0.001);
	}


	@Test
	@DisplayName("parseToDouble - null 문자열 파싱 시 null 반환")
	void testParseToDouble_nullString() throws Exception {
		Double result = (Double) ReflectionTestUtils.invokeMethod(storeDataService, "parseToDouble", (String) null);
		assertNull(result);
	}

	@Test
	@DisplayName("parseToDouble - 빈 문자열 파싱 시 null 반환")
	void testParseToDouble_emptyString() throws Exception {
		Double result = (Double) ReflectionTestUtils.invokeMethod(storeDataService, "parseToDouble", "");
		assertNull(result);
	}

	@Test
	@DisplayName("parseToDouble - 유효하지 않은 숫자 형식 파싱 시 null 반환")
	void testParseToDouble_invalidNumberFormat() throws Exception {
		Double result = (Double) ReflectionTestUtils.invokeMethod(storeDataService, "parseToDouble", "abc");
		assertNull(result);
	}
}

