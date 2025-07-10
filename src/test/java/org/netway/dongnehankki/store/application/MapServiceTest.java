package org.netway.dongnehankki.store.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.StoreRepository;

@ExtendWith(MockitoExtension.class)
public class MapServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private MapService mapService;

	private static final double KM_PER_LATITUDE_DEGREE = 111.0;
	private static final double KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE = 111.0 * Math.cos(Math.toRadians(37.5));

	private static final double CENTER_LAT = 37.5665;
	private static final double CENTER_LON = 126.9780;

	@BeforeEach
	void setUp() {
		reset(storeRepository);

		double lat_0_2km = CENTER_LAT + (0.2 / KM_PER_LATITUDE_DEGREE);
		double lon_0_2km = CENTER_LON + (0.2 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		Store.createStore("500m내 가게", lat_0_2km, lon_0_2km, "주소1", "음식점", "F1", "10-111-2222");

		double lat_1_5km = CENTER_LAT + (1.5 / KM_PER_LATITUDE_DEGREE);
		double lon_1_5km = CENTER_LON + (1.5 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		Store.createStore("2km내 가게", lat_1_5km, lon_1_5km, "주소2", "카페", "F2", "10-333-4444");

		double lat_4km = CENTER_LAT + (4.0 / KM_PER_LATITUDE_DEGREE);
		double lon_4km = CENTER_LON + (4.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		Store.createStore("5km내 가게", lat_4km, lon_4km, "주소3", "마트", "F3", "10-555-6666");

		double lat_6km = CENTER_LAT + (6.0 / KM_PER_LATITUDE_DEGREE);
		double lon_6km = CENTER_LON + (6.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		Store.createStore("7km내 가게", lat_6km, lon_6km, "주소4", "병원", "F4", "10-777-8888");

		double lat_9km = CENTER_LAT + (9.0 / KM_PER_LATITUDE_DEGREE);
		double lon_9km = CENTER_LON + (9.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		Store.createStore("10km내 가게", lat_9km, lon_9km, "주소5", "약국", "F5", "10-999-0000");

		double lat_12km = CENTER_LAT + (12.0 / KM_PER_LATITUDE_DEGREE);
		double lon_12km = CENTER_LON + (12.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		Store storeBeyond10km = Store.createStore("10km밖 가게", lat_12km, lon_12km, "주소6", "미분류", "F6", "10-123-5678");
	}

	@DisplayName("범위 내에 가게가 없을 경우, 빈 리스트 반환")
	@Test
	void getLocalCurrencyStores_noStoresFound() {
		MapRequest request = MapRequest.builder().latitude(37.0).longitude(127.0).zoomLevel(5).build();

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(
			anyDouble(), anyDouble(), anyDouble(), anyDouble()))
			.thenReturn(Collections.emptyList());

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).isEmpty();
	}

	@DisplayName("유효하지 않은 줌 레벨이 들어오면, 빈 리스트 반환")
	@Test
	void getStoresOnMap_invalidZoomLevel_usesDefaultBounds() {
		MapRequest request = MapRequest.builder().latitude(37.0).longitude(127.0).zoomLevel(0).build();

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).isEmpty();
	}

	@DisplayName("줌 레벨 5 (0.5km 반경)일 때, 500m 내 가게 반환")
	@Test
	void getStoresByMap_zoomLevel5_returnsOnlyWithin500m() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(5).build();

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(1);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게");
	}

	@DisplayName("줌 레벨 4 (2km 반경)일 때, 500m내, 2km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel4_returnsWithin2km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(4).build();

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(2);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게");
	}

	@DisplayName("줌 레벨 3 (5km 반경)일 때, 500m내, 2km내, 5km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel3_returnsWithin5km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(3).build();

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(3);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게");
	}

	@DisplayName("줌 레벨 2 (7km 반경)일 때, 500m내, 2km내, 5km내, 7km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel2_returnsWithin7km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(2).build();

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(4);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게", "7km내 가게");
	}

	@DisplayName("줌 레벨 1 (10km 반경)일 때, 500m내, 2km내, 5km내, 7km내, 10km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel1_returnsWithin10km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(1).build();

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(5);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게", "7km내 가게", "10km내 가게");
	}
}
