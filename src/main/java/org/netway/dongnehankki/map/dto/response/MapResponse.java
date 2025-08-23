package org.netway.dongnehankki.map.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.netway.dongnehankki.store.domain.Store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapResponse {
	private Long storeId;
	private String name;
	private Double latitude;
	private Double longitude;
	private Integer likeCount;
	private String sigun;
	private String address;
	private Integer industryCode;
	private Double avgStar;
	private List<OperatingHourResponse> operatingHours;

	public static MapResponse fromEntity(Store store) {
		List<OperatingHourResponse> operatingHours = store.getOperatingHours()
			.stream().map(OperatingHourResponse::fromEntity).toList();

		return MapResponse.builder()
			.storeId(store.getStoreId())
			.name(store.getName())
			.latitude(store.getLatitude())
			.longitude(store.getLongitude())
			.sigun(store.getSigun())
			.address(store.getAddress())
			.industryCode(store.getIndustryCode())
			.avgStar(store.getAverageStar())
			.operatingHours(operatingHours)
			.build();
	}
}









