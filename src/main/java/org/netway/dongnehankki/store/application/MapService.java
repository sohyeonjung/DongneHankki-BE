package org.netway.dongnehankki.store.application;

import java.util.List;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.request.MapRequest;
import org.netway.dongnehankki.store.dto.response.MapResponse;
import org.netway.dongnehankki.store.infrastructure.StoreRepository;
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

		List<Store> stores = storeRepository.findByLatitudeBetweenAndLongitudeBetween(
			boundingBox.getMinLat(), boundingBox.getMaxLat(),
			boundingBox.getMinLon(), boundingBox.getMaxLon()
		);

		return stores.stream().map(MapResponse::fromEntity).toList();
	}
}
