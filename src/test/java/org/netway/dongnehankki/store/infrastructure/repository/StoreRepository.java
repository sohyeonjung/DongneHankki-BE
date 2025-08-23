package org.netway.dongnehankki.store.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.store.domain.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StoreRepositoryTest {

	@Autowired
	private StoreRepository storeRepository;

	private Store storeA;
	private Store storeB;
	private Store storeC;
	private Store storeD;

	@BeforeEach
	void setUp() {
		storeRepository.deleteAll();

		storeA = storeRepository.save(
			Store.createStore("가게A", 37.5665, 126.9780, "경기도 광명시 A"
			,"경기도 광명시", 1234, 123456789L));


		storeB = storeRepository.save(
			Store.createStore("가게B", 37.5670, 126.9785,
				"서울", "서울시", 1234, 98765421L));

		storeC = storeRepository.save(
			Store.createStore("가게C", 37.6000, 126.9800,
				"서울", "서울시", 1244, 1112233333L));

		storeD = storeRepository.save(
			Store.createStore("가게D", 37.4000, 127.0000,
				"강원도", "강원시", 1234, 444555666L));
	}

	@DisplayName("findByStoreId - 존재하는 가게 ID로 조회 성공")
	@Test
	void findByStoreId_existingId() {
		// When
		Optional<Store> foundStoreOptional = storeRepository.findByStoreId(storeA.getStoreId());

		// Then
		assertThat(foundStoreOptional).isPresent();
		assertThat(foundStoreOptional.get().getName()).isEqualTo("가게A");
		assertThat(foundStoreOptional.get().getBusinessRegistrationNumber()).isEqualTo(123456789L);
	}


	@DisplayName("findByStoreId - 존재하지 않는 가게 ID로 조회 시 빈 Optional 반환")
	@Test
	void findByStoreId_nonExistingId() {
		// Given
		Long nonExistingId = 999L;

		// When
		Optional<Store> foundStoreOptional = storeRepository.findByStoreId(nonExistingId);

		// Then
		assertThat(foundStoreOptional).isEmpty();
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetween - 위도와 경도 범위 내의 가게 목록 조회")
	@Test
	void findStoresWithinCoordinates() {
		double minLat = 37.5660;
		double maxLat = 37.5675;
		double minLon = 126.9775;
		double maxLon = 126.9790;

		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetween(
			minLat, maxLat, minLon, maxLon);

		assertThat(stores).hasSize(2);
		assertThat(stores).extracting(Store::getName)
			.containsExactlyInAnyOrder("가게A", "가게B");
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetween - 범위 내에 가게가 없을 경우 빈 리스트를 반환")
	@Test
	void findStoresWhenNoneExistWithinCoordinates() {
		double minLat = 30.0;
		double maxLat = 31.0;
		double minLon = 100.0;
		double maxLon = 101.0;

		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetween(
			minLat, maxLat, minLon, maxLon);

		assertThat(stores).isEmpty();
	}


	@DisplayName("findByBusinessRegistrationNumber - 존재하는 사업자등록번호로 조회 성공")
	@Test
	void findByBusinessRegistrationNumber_existingNumber() {
		// When
		Optional<Store> foundStoreOptional = storeRepository.findByBusinessRegistrationNumber(98765421L);

		// Then
		assertThat(foundStoreOptional).isPresent();
		assertThat(foundStoreOptional.get().getName()).isEqualTo("가게B");
		assertThat(foundStoreOptional.get().getStoreId()).isEqualTo(storeB.getStoreId());
	}

	@DisplayName("findByBusinessRegistrationNumber - 존재하지 않는 사업자등록번호로 조회 시 빈 Optional 반환")
	@Test
	void findByBusinessRegistrationNumber_nonExistingNumber() {
		// Given
		Long nonExistingNumber = 9999999999L;

		// When
		Optional<Store> foundStoreOptional = storeRepository.findByBusinessRegistrationNumber(Long.valueOf(nonExistingNumber));

		// Then
		assertThat(foundStoreOptional).isEmpty();
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetweenAndIndutypeCd - 범위 내 특정 업종의 가게만 조회")
	@Test
	void findStoresWithinCoordinatesAndIndustryCode() {
		// Given
		double minLat = 37.5660;
		double maxLat = 37.5675;
		double minLon = 126.9775;
		double maxLon = 126.9790;
		Integer targetIndustryCode = 1234;

		// When
		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
			minLat, maxLat, minLon, maxLon, targetIndustryCode);

		// Then
		assertThat(stores).hasSize(2);
		assertThat(stores).extracting(Store::getName).containsExactlyInAnyOrder("가게A", "가게B");
		assertThat(stores).allMatch(store -> store.getIndustryCode().equals(targetIndustryCode));
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetweenAndIndutypeCd - 범위 내에 있지만 업종이 다른 가게는 제외")
	@Test
	void findStoresExcludingDifferentIndustryCode() {
		// Given
		double minLat = 37.5600;
		double maxLat = 37.6100;
		double minLon = 126.9700;
		double maxLon = 126.9900;
		Integer targetIndustryCode = 1234;

		// When
		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
			minLat, maxLat, minLon, maxLon, targetIndustryCode);

		// Then
		assertThat(stores).hasSize(2);
		assertThat(stores).extracting(Store::getName).containsExactlyInAnyOrder("가게A", "가게B");
		assertThat(stores).allMatch(store -> store.getIndustryCode().equals(targetIndustryCode));
	}


	@DisplayName("findByLatitudeBetweenAndLongitudeBetweenAndIndutypeCd - 특정 업종의 가게가 범위 내에 없을 경우 빈 리스트 반환")
	@Test
	void findStoresWhenNoMatchingIndustryCodeWithinCoordinates() {
		// Given
		double minLat = 37.5660;
		double maxLat = 37.5675;
		double minLon = 126.9775;
		double maxLon = 126.9790;
		Integer nonExistingIndustryCode = 9999;

		// When
		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
			minLat, maxLat, minLon, maxLon, nonExistingIndustryCode);

		// Then
		assertThat(stores).isEmpty();
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetweenAndIndutypeCd - 업종은 일치하지만 범위 밖에 있는 가게는 제외")
	@Test
	void findStoresExcludingOutOfRangeButMatchingIndustryCode() {
		// Given
		double minLat = 37.5660;
		double maxLat = 37.5675;
		double minLon = 126.9775;
		double maxLon = 126.9790;
		Integer targetIndustryCode = 1234;

		// When
		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
			minLat, maxLat, minLon, maxLon, targetIndustryCode);

		// Then
		assertThat(stores).hasSize(2);
		assertThat(stores).extracting(Store::getName).containsExactlyInAnyOrder("가게A", "가게B");
		assertThat(stores).doesNotContain(storeD);
		assertThat(stores).allMatch(store -> store.getIndustryCode().equals(targetIndustryCode));
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetweenAndNameContaining - 위치와 이름으로 필터링")
	@Test
	void findByCoordinatesAndName() {
		// Given
		double minLat = 37.5660;
		double maxLat = 37.5675;
		double minLon = 126.9775;
		double maxLon = 126.9790;
		String keyword = "A";

		// When
		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndNameContaining(
			minLat, maxLat, minLon, maxLon, keyword
		);

		// Then
		assertThat(stores).hasSize(1);
		assertThat(stores).extracting(Store::getName)
			.containsExactlyInAnyOrder("가게A");
	}

	@DisplayName("findByLatitudeBetweenAndLongitudeBetweenAndIndustryCodeAndNameContaining - 위치, 업종, 이름으로 필터링")
	@Test
	void findByCoordinatesAndIndustryCodeAndName() {
		// Given
		double minLat = 37.5660;
		double maxLat = 37.5675;
		double minLon = 126.9775;
		double maxLon = 126.9790;
		Integer targetIndustryCode = 1234;
		String keyword = "가게";

		// When
		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCodeAndNameContaining(
			minLat, maxLat, minLon, maxLon, targetIndustryCode, keyword
		);

		// Then
		assertThat(stores).hasSize(2);
		assertThat(stores).extracting(Store::getName)
			.containsExactlyInAnyOrder("가게A", "가게B");
		assertThat(stores).allMatch(store -> store.getIndustryCode().equals(targetIndustryCode));
	}
}


