package org.netway.dongnehankki.store.application;

import org.springframework.stereotype.Component;

@Component
public class MapBoundaryCalculator {

	private static final double KM_PER_LATITUDE_DEGREE = 111.0;
	private static final double KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE = 111.0 * Math.cos(Math.toRadians(37.5));

	public MapBoundingBox calculateBoundingBox(double centerLat, double centerLon, Integer zoomLevel) {
		double radiusKm;

		switch (zoomLevel) {
			case 2:
				radiusKm = 7.0;
				break;
			case 3:
				radiusKm = 5.0;
				break;
			case 4:
				radiusKm = 2.0;
				break;
			case 5:
				radiusKm = 0.5;
				break;
			default:
				radiusKm = 10.0;
				break;
		}

		double deltaLatDegree = radiusKm / KM_PER_LATITUDE_DEGREE;
		double deltaLonDegree = radiusKm / (KM_PER_LONGITUDE_DEGREE_AT_SEOUL_LATITUDE);

		double minLat = centerLat - deltaLatDegree;
		double maxLat = centerLat + deltaLatDegree;
		double minLon = centerLon - deltaLonDegree;
		double maxLon = centerLon + deltaLonDegree;

		return MapBoundingBox.builder().minLat(minLat).maxLat(maxLat).minLon(minLon).maxLon(maxLon).build();
	}
}
