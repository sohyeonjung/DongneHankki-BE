package org.netway.dongnehankki.map.application;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.netway.dongnehankki.map.dto.request.MapRequest;
import org.netway.dongnehankki.map.dto.response.MapResponse;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService{
	private final StoreRepository storeRepository;
	private final MapBoundaryCalculator mapBoundaryCalculator;

	@Transactional(readOnly = true)
	public List<MapResponse> getStoresOnMap(MapRequest mapRequest) {
		MapBoundingBox boundingBox = mapBoundaryCalculator.calculateBoundingBox(
			mapRequest.getLatitude(), mapRequest.getLongitude(), mapRequest.getZoomLevel()
		);

		List<Store> stores;

		Integer industryCode = mapRequest.getIndustryCode();
		//TODO: 검색 필터링 querydsl로 변경
		String keyword = mapRequest.getKeyword();

		if (keyword != null && !keyword.isBlank() && industryCode != null) {
			stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCodeAndNameContaining(
				boundingBox.getMinLat(), boundingBox.getMaxLat(),
				boundingBox.getMinLon(), boundingBox.getMaxLon(),
				industryCode,
				keyword
			);
		} else if (keyword != null && !keyword.isBlank()) {
			stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndNameContaining(
				boundingBox.getMinLat(), boundingBox.getMaxLat(),
				boundingBox.getMinLon(), boundingBox.getMaxLon(),
				keyword
			);
		} else if (industryCode != null) {
			stores = storeRepository.findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
				boundingBox.getMinLat(), boundingBox.getMaxLat(),
				boundingBox.getMinLon(), boundingBox.getMaxLon(),
				industryCode
			);
		} else {
			stores = storeRepository.findByLatitudeBetweenAndLongitudeBetween(
				boundingBox.getMinLat(), boundingBox.getMaxLat(),
				boundingBox.getMinLon(), boundingBox.getMaxLon()
			);
		}

		Integer scope = mapRequest.getScope();
		if(scope != null){
			stores = stores.stream().filter(it -> it.getAverageStar() >= scope).toList();
		}

		if (mapRequest.getDays() != null || mapRequest.getStartAt() != null || mapRequest.getEndAt() != null){
			List<DayOfWeek> days = (mapRequest.getDays() == null || mapRequest.getDays().isEmpty())
				? List.of(DayOfWeek.values()) : mapRequest.getDays();
			LocalTime startAt = (mapRequest.getStartAt() == null) ? LocalTime.MIN : mapRequest.getStartAt();
			LocalTime endAt = (mapRequest.getEndAt() == null) ? LocalTime.MAX : mapRequest.getEndAt();

			stores = stores.stream()
				.filter(store -> store.getOperatingHours().stream()
					.anyMatch(openHour ->
						days.contains(openHour.getDayOfWeek()) &&
							!openHour.getOpenTime().isAfter(startAt) &&
							!openHour.getCloseTime().isBefore(endAt)
					)).toList();
		}

		return stores.stream().map(MapResponse::fromEntity).toList();
	}
}
