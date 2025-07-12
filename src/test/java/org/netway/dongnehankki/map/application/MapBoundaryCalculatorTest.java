package org.netway.dongnehankki.map.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MapBoundaryCalculatorTest {

	private MapBoundaryCalculator mapBoundaryCalculator;

	private static final double KM_PER_LATITUDE_DEGREE = 111.0;
	private static final double KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE = 111.0 * Math.cos(Math.toRadians(37.5));

	private static final double TEST_CENTER_LAT = 37.5665;
	private static final double TEST_CENTER_LON = 126.9780;

	@BeforeEach
	void setUp() {
		mapBoundaryCalculator = new MapBoundaryCalculator();
	}

	@DisplayName("줌 레벨 5 (0.5km 반경)일 때 올바른 경계 상자 계산")
	@Test
	void calculateBoundingBox_zoomLevel5_correctBounds() {
		int zoomLevel = 5;
		double expectedRadiusKm = 0.5;

		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			TEST_CENTER_LAT, TEST_CENTER_LON, zoomLevel
		);

		double expectedDeltaLatDegree = expectedRadiusKm / KM_PER_LATITUDE_DEGREE;
		double expectedDeltaLonDegree = expectedRadiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		assertThat(boundingBox.getMinLat()).isEqualTo(TEST_CENTER_LAT - expectedDeltaLatDegree);
		assertThat(boundingBox.getMaxLat()).isEqualTo(TEST_CENTER_LAT + expectedDeltaLatDegree);
		assertThat(boundingBox.getMinLon()).isEqualTo(TEST_CENTER_LON - expectedDeltaLonDegree);
		assertThat(boundingBox.getMaxLon()).isEqualTo(TEST_CENTER_LON + expectedDeltaLonDegree);
	}

	@DisplayName("줌 레벨 4 (2km 반경)일 때 올바른 경계 상자 계산")
	@Test
	void calculateBoundingBox_zoomLevel4_correctBounds() {
		int zoomLevel = 4;
		double expectedRadiusKm = 2.0;

		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			TEST_CENTER_LAT, TEST_CENTER_LON, zoomLevel
		);

		double expectedDeltaLatDegree = expectedRadiusKm / KM_PER_LATITUDE_DEGREE;
		double expectedDeltaLonDegree = expectedRadiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		assertThat(boundingBox.getMinLat()).isEqualTo(TEST_CENTER_LAT - expectedDeltaLatDegree);
		assertThat(boundingBox.getMaxLat()).isEqualTo(TEST_CENTER_LAT + expectedDeltaLatDegree);
		assertThat(boundingBox.getMinLon()).isEqualTo(TEST_CENTER_LON - expectedDeltaLonDegree);
		assertThat(boundingBox.getMaxLon()).isEqualTo(TEST_CENTER_LON + expectedDeltaLonDegree);
	}

	@DisplayName("줌 레벨 3 (5km 반경)일 때 올바른 경계 상자 계산")
	@Test
	void calculateBoundingBox_zoomLevel3_correctBounds() {
		int zoomLevel = 3;
		double expectedRadiusKm = 5.0;

		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			TEST_CENTER_LAT, TEST_CENTER_LON, zoomLevel
		);

		double expectedDeltaLatDegree = expectedRadiusKm / KM_PER_LATITUDE_DEGREE;
		double expectedDeltaLonDegree = expectedRadiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		assertThat(boundingBox.getMinLat()).isEqualTo(TEST_CENTER_LAT - expectedDeltaLatDegree);
		assertThat(boundingBox.getMaxLat()).isEqualTo(TEST_CENTER_LAT + expectedDeltaLatDegree);
		assertThat(boundingBox.getMinLon()).isEqualTo(TEST_CENTER_LON - expectedDeltaLonDegree);
		assertThat(boundingBox.getMaxLon()).isEqualTo(TEST_CENTER_LON + expectedDeltaLonDegree);
	}

	@DisplayName("줌 레벨 2 (7km 반경)일 때 올바른 경계 상자 계산")
	@Test
	void calculateBoundingBox_zoomLevel2_correctBounds() {
		int zoomLevel = 2;
		double expectedRadiusKm = 7.0;

		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			TEST_CENTER_LAT, TEST_CENTER_LON, zoomLevel
		);

		double expectedDeltaLatDegree = expectedRadiusKm / KM_PER_LATITUDE_DEGREE;
		double expectedDeltaLonDegree = expectedRadiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		assertThat(boundingBox.getMinLat()).isEqualTo(TEST_CENTER_LAT - expectedDeltaLatDegree);
		assertThat(boundingBox.getMaxLat()).isEqualTo(TEST_CENTER_LAT + expectedDeltaLatDegree);
		assertThat(boundingBox.getMinLon()).isEqualTo(TEST_CENTER_LON - expectedDeltaLonDegree);
		assertThat(boundingBox.getMaxLon()).isEqualTo(TEST_CENTER_LON + expectedDeltaLonDegree);
	}

	@DisplayName("줌 레벨 1 (10km 반경)일 때 올바른 경계 상자 계산")
	@Test
	void calculateBoundingBox_zoomLevel1_correctBounds() {
		int zoomLevel = 1;
		double expectedRadiusKm = 10.0;

		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			TEST_CENTER_LAT, TEST_CENTER_LON, zoomLevel
		);

		double expectedDeltaLatDegree = expectedRadiusKm / KM_PER_LATITUDE_DEGREE;
		double expectedDeltaLonDegree = expectedRadiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		assertThat(boundingBox.getMinLat()).isEqualTo(TEST_CENTER_LAT - expectedDeltaLatDegree);
		assertThat(boundingBox.getMaxLat()).isEqualTo(TEST_CENTER_LAT + expectedDeltaLatDegree);
		assertThat(boundingBox.getMinLon()).isEqualTo(TEST_CENTER_LON - expectedDeltaLonDegree);
		assertThat(boundingBox.getMaxLon()).isEqualTo(TEST_CENTER_LON + expectedDeltaLonDegree);
	}

	@DisplayName("유효하지 않은 줌 레벨일 때 기본값(10km 반경)으로 올바른 경계 상자 계산")
	@Test
	void calculateBoundingBox_invalidZoomLevel_usesDefaultBounds() {
		int invalidZoomLevel = 0;
		double expectedRadiusKm = 10.0;

		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			TEST_CENTER_LAT, TEST_CENTER_LON, invalidZoomLevel
		);

		double expectedDeltaLatDegree = expectedRadiusKm / KM_PER_LATITUDE_DEGREE;
		double expectedDeltaLonDegree = expectedRadiusKm / KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE;

		assertThat(boundingBox.getMinLat()).isEqualTo(TEST_CENTER_LAT - expectedDeltaLatDegree);
		assertThat(boundingBox.getMaxLat()).isEqualTo(TEST_CENTER_LAT + expectedDeltaLatDegree);
		assertThat(boundingBox.getMinLon()).isEqualTo(TEST_CENTER_LON - expectedDeltaLonDegree);
		assertThat(boundingBox.getMaxLon()).isEqualTo(TEST_CENTER_LON + expectedDeltaLonDegree);
	}
}