package org.netway.dongnehankki.store.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

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

	@BeforeEach
	void setUp() {
		storeRepository.deleteAll();

		storeRepository.save(
			Store.createStore("가게A", 37.5665, 126.9780, "경기도 광명시 A"
			,"경기도 광명시", "F1", "123-45-6789a0"));


		storeRepository.save(
			Store.createStore("가게B", 37.5670, 126.9785,
				"서울", "서울시", "F2", "098-76-54321"));

		storeRepository.save(
			Store.createStore("가게C", 37.6000, 126.9800,
				"서울", "서울시", "R1", "111-22-33333"));
	}

	@DisplayName("위도와 경도 범위 내의 가게 목록 조회")
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

	@DisplayName("범위 내에 가게가 없을 경우 빈 리스트를 반환")
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

}
