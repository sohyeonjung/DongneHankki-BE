package org.netway.dongnehankki.map.application;

import java.util.List;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.map.dto.request.MapRequest;
import org.netway.dongnehankki.map.dto.response.MapResponse;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

	private final StoreRepository storeRepository;
	private final MapBoundaryCalculator mapBoundaryCalculator;

	public List<MapResponse> getStoresOnMap(MapRequest mapRequest) {
		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			mapRequest.getLatitude(), mapRequest.getLongitude(), mapRequest.getZoomLevel()
		);

		List<Store> stores;

		Integer industryCode = mapRequest.getIndustryCode();
		if (industryCode == null) {
			stores = storeRepository.findByLatitudeBetweenAndLongitudeBetween(
				boundingBox.getMinLat(), boundingBox.getMaxLat(),
				boundingBox.getMinLon(), boundingBox.getMaxLon()
			);
		}
		else{
			stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
				boundingBox.getMinLat(), boundingBox.getMaxLat(),
				boundingBox.getMinLon(), boundingBox.getMaxLon(),
				mapRequest.getIndustryCode()
			);
		}

		return stores.stream().map(MapResponse::fromEntity).toList();
	}
}
