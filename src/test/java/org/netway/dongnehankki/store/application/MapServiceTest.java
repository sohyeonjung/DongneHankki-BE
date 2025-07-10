package org.netway.dongnehankki.store.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.request.MapRequest;
import org.netway.dongnehankki.store.dto.response.MapResponse;
import org.netway.dongnehankki.store.infrastructure.StoreRepository;

@ExtendWith(MockitoExtension.class)
public class MapServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MapBoundaryCalculator mapBoundaryCalculator;

	@InjectMocks
	private MapService mapService;

	private static final double KM_PER_LATITUDE_DEGREE = 111.0;
	private static final double KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE = 111.0 * Math.cos(Math.toRadians(37.5));

	private static final double CENTER_LAT = 37.5665;
	private static final double CENTER_LON = 126.9780;

	private Store storeWithin500m;
	private Store storeWithin2km;
	private Store storeWithin5km;
	private Store storeWithin7km;
	private Store storeWithin10km;
	private Store storeBeyond10km;

	@BeforeEach
	void setUp() {
		reset(storeRepository);
		reset(mapBoundaryCalculator);
		double lat_0_2km = CENTER_LAT + (0.2 / KM_PER_LATITUDE_DEGREE);
		double lon_0_2km = CENTER_LON + (0.2 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		storeWithin500m = Store.createStore("500m내 가게", lat_0_2km, lon_0_2km, "주소1", "음식점", "F1", "10-111-2222");

		double lat_1_5km = CENTER_LAT + (1.5 / KM_PER_LATITUDE_DEGREE);
		double lon_1_5km = CENTER_LON + (1.5 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		storeWithin2km = Store.createStore("2km내 가게", lat_1_5km, lon_1_5km, "주소2", "카페", "F2", "10-333-4444");

		double lat_4km = CENTER_LAT + (4.0 / KM_PER_LATITUDE_DEGREE);
		double lon_4km = CENTER_LON + (4.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		storeWithin5km = Store.createStore("5km내 가게", lat_4km, lon_4km, "주소3", "마트", "F3", "10-555-6666");

		double lat_6km = CENTER_LAT + (6.0 / KM_PER_LATITUDE_DEGREE);
		double lon_6km = CENTER_LON + (6.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		storeWithin7km = Store.createStore("7km내 가게", lat_6km, lon_6km, "주소4", "병원", "F4", "10-777-8888");

		double lat_9km = CENTER_LAT + (9.0 / KM_PER_LATITUDE_DEGREE);
		double lon_9km = CENTER_LON + (9.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		storeWithin10km = Store.createStore("10km내 가게", lat_9km, lon_9km, "주소5", "약국", "F5", "10-999-0000");

		double lat_12km = CENTER_LAT + (12.0 / KM_PER_LATITUDE_DEGREE);
		double lon_12km = CENTER_LON + (12.0 / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);
		storeBeyond10km = Store.createStore("10km밖 가게", lat_12km, lon_12km, "주소6", "미분류", "F6", "10-123-5678");
	}

	@DisplayName("범위 내에 가게가 없을 경우, 빈 리스트 반환")
	@Test
	void getLocalCurrencyStores_noStoresFound() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(5).build();

		MapBoundingBox dummyBoundingBox = MapBoundingBox.builder().minLat(CENTER_LAT - 0.1).maxLat(CENTER_LAT + 0.1)
			.minLon(CENTER_LON - 0.1).maxLon(CENTER_LON + 0.1).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(dummyBoundingBox);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
			.thenReturn(Collections.emptyList());

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).isEmpty();
		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 5);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(dummyBoundingBox.getMinLat(), dummyBoundingBox.getMaxLat(),
			dummyBoundingBox.getMinLon(), dummyBoundingBox.getMaxLon());
	}

	@DisplayName("유효하지 않은 줌 레벨이 들어오면, 기본값(10km) 범위의 가게 반환")
	@Test
	void getStoresOnMap_invalidZoomLevel_usesDefaultBounds() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(0).build();

		double radiusKm = 10.0;
		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		MapBoundingBox defaultBoundingBox = MapBoundingBox.builder().minLat(CENTER_LAT - deltaLatDegree).maxLat(CENTER_LAT + deltaLatDegree)
			.minLon(CENTER_LON - deltaLonDegree).maxLon(CENTER_LON + deltaLonDegree).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(defaultBoundingBox);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(defaultBoundingBox.getMinLat(), defaultBoundingBox.getMaxLat(),
			defaultBoundingBox.getMinLon(), defaultBoundingBox.getMaxLon()))
			.thenReturn(Arrays.asList(storeWithin500m, storeWithin2km, storeWithin5km, storeWithin7km, storeWithin10km));

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(5);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게", "7km내 가게", "10km내 가게");

		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 0);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(defaultBoundingBox.getMinLat(), defaultBoundingBox.getMaxLat(),
			defaultBoundingBox.getMinLon(), defaultBoundingBox.getMaxLon());
	}

	@DisplayName("줌 레벨 5 (0.5km 반경)일 때, 500m 내 가게 반환")
	@Test
	void getStoresByMap_zoomLevel5_returnsOnlyWithin500m() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(5).build();

		double radiusKm = 0.5;
		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		MapBoundingBox boundingBox500m = MapBoundingBox.builder().minLat(CENTER_LAT - deltaLatDegree).maxLat(CENTER_LAT + deltaLatDegree)
			.minLon(CENTER_LON - deltaLonDegree).maxLon(CENTER_LON + deltaLonDegree).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(boundingBox500m);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(boundingBox500m.getMinLat(), boundingBox500m.getMaxLat(),
			boundingBox500m.getMinLon(), boundingBox500m.getMaxLon()))
			.thenReturn(Collections.singletonList(storeWithin500m));

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(1);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게");

		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 5);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(boundingBox500m.getMinLat(), boundingBox500m.getMaxLat(),
			boundingBox500m.getMinLon(), boundingBox500m.getMaxLon());
	}

	@DisplayName("줌 레벨 4 (2km 반경)일 때, 500m내, 2km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel4_returnsWithin2km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(4).build();

		double radiusKm = 2.0;
		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		MapBoundingBox boundingBox2km = MapBoundingBox.builder().minLat(CENTER_LAT - deltaLatDegree).maxLat(CENTER_LAT + deltaLatDegree)
			.minLon(CENTER_LON - deltaLonDegree).maxLon(CENTER_LON + deltaLonDegree).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(boundingBox2km);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(boundingBox2km.getMinLat(), boundingBox2km.getMaxLat(),
			boundingBox2km.getMinLon(), boundingBox2km.getMaxLon()))
			.thenReturn(Arrays.asList(storeWithin500m, storeWithin2km));

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(2);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게");

		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 4);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(boundingBox2km.getMinLat(), boundingBox2km.getMaxLat(),
			boundingBox2km.getMinLon(), boundingBox2km.getMaxLon());
	}

	@DisplayName("줌 레벨 3 (5km 반경)일 때, 500m내, 2km내, 5km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel3_returnsWithin5km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(3).build();

		double radiusKm = 5.0;
		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		MapBoundingBox boundingBox5km = MapBoundingBox.builder().minLat(CENTER_LAT - deltaLatDegree).maxLat(CENTER_LAT + deltaLatDegree)
			.minLon(CENTER_LON - deltaLonDegree).maxLon(CENTER_LON + deltaLonDegree).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(boundingBox5km);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(boundingBox5km.getMinLat(), boundingBox5km.getMaxLat(),
			boundingBox5km.getMinLon(), boundingBox5km.getMaxLon()))
			.thenReturn(Arrays.asList(storeWithin500m, storeWithin2km, storeWithin5km));

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(3);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게");

		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 3);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(boundingBox5km.getMinLat(), boundingBox5km.getMaxLat(),
			boundingBox5km.getMinLon(), boundingBox5km.getMaxLon());
	}

	@DisplayName("줌 레벨 2 (7km 반경)일 때, 500m내, 2km내, 5km내, 7km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel2_returnsWithin7km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(2).build();

		double radiusKm = 7.0;
		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		MapBoundingBox boundingBox7km = MapBoundingBox.builder().minLat(CENTER_LAT - deltaLatDegree).maxLat(CENTER_LAT + deltaLatDegree)
			.minLon(CENTER_LON - deltaLonDegree).maxLon(CENTER_LON + deltaLonDegree).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(boundingBox7km);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(boundingBox7km.getMinLat(), boundingBox7km.getMaxLat(),
			boundingBox7km.getMinLon(), boundingBox7km.getMaxLon()))
			.thenReturn(Arrays.asList(storeWithin500m, storeWithin2km, storeWithin5km, storeWithin7km));

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(4);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게", "7km내 가게");

		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 2);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(boundingBox7km.getMinLat(), boundingBox7km.getMaxLat(),
			boundingBox7km.getMinLon(), boundingBox7km.getMaxLon());
	}

	@DisplayName("줌 레벨 1 (10km 반경)일 때, 500m내, 2km내, 5km내, 7km내, 10km내 가게 반환")
	@Test
	void getStoresOnMap_zoomLevel1_returnsWithin10km() {
		MapRequest request = MapRequest.builder().latitude(CENTER_LAT).longitude(CENTER_LON).zoomLevel(1).build();

		double radiusKm = 10.0;
		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		MapBoundingBox boundingBox10km = MapBoundingBox.builder().minLat(CENTER_LAT - deltaLatDegree).maxLat(CENTER_LAT + deltaLatDegree)
			.minLon(CENTER_LON - deltaLonDegree).maxLon(CENTER_LON + deltaLonDegree).build();

		when(mapBoundaryCalculator.calculateBoundingBox(anyDouble(), anyDouble(), anyInt()))
			.thenReturn(boundingBox10km);

		when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(boundingBox10km.getMinLat(), boundingBox10km.getMaxLat(),
			boundingBox10km.getMinLon(), boundingBox10km.getMaxLon()))
			.thenReturn(Arrays.asList(storeWithin500m, storeWithin2km, storeWithin5km, storeWithin7km, storeWithin10km));

		List<MapResponse> result = mapService.getStoresOnMap(request);

		assertThat(result).hasSize(5);
		assertThat(result).extracting(MapResponse::getName)
			.containsExactlyInAnyOrder("500m내 가게", "2km내 가게", "5km내 가게", "7km내 가게", "10km내 가게");

		verify(mapBoundaryCalculator).calculateBoundingBox(CENTER_LAT, CENTER_LON, 1);
		verify(storeRepository).findByLatitudeBetweenAndLongitudeBetween(boundingBox10km.getMinLat(), boundingBox10km.getMaxLat(),
			boundingBox10km.getMinLon(), boundingBox10km.getMaxLon());
	}
}