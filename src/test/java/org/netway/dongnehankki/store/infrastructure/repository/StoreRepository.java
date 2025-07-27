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

	@BeforeEach
	void setUp() {
		storeRepository.deleteAll();

		storeA = storeRepository.save(
			Store.createStore("가게A", 37.5665, 126.9780, "경기도 광명시 A"
			,"경기도 광명시", "F1", "123-45-6789a0"));


		storeB = storeRepository.save(
			Store.createStore("가게B", 37.5670, 126.9785,
				"서울", "서울시", "F2", "098-76-54321"));

		storeC = storeRepository.save(
			Store.createStore("가게C", 37.6000, 126.9800,
				"서울", "서울시", "R1", "111-22-33333"));
	}

	@DisplayName("findByStoreId - 존재하는 가게 ID로 조회 성공")
	@Test
	void findByStoreId_existingId() {
		// When
		Optional<Store> foundStoreOptional = storeRepository.findByStoreId(storeA.getStoreId());

		// Then
		assertThat(foundStoreOptional).isPresent();
		assertThat(foundStoreOptional.get().getName()).isEqualTo("가게A");
		assertThat(foundStoreOptional.get().getBusinessRegistrationNumber()).isEqualTo("123-45-6789a0");
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
		Optional<Store> foundStoreOptional = storeRepository.findByBusinessRegistrationNumber("098-76-54321");

		// Then
		assertThat(foundStoreOptional).isPresent();
		assertThat(foundStoreOptional.get().getName()).isEqualTo("가게B");
		assertThat(foundStoreOptional.get().getStoreId()).isEqualTo(storeB.getStoreId());
	}

	@DisplayName("findByBusinessRegistrationNumber - 존재하지 않는 사업자등록번호로 조회 시 빈 Optional 반환")
	@Test
	void findByBusinessRegistrationNumber_nonExistingNumber() {
		// Given
		String nonExistingNumber = "999-99-99999";

		// When
		Optional<Store> foundStoreOptional = storeRepository.findByBusinessRegistrationNumber(nonExistingNumber);

		// Then
		assertThat(foundStoreOptional).isEmpty();
	}

}
