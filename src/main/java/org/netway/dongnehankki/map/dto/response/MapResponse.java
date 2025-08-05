package org.netway.dongnehankki.map.dto.response;

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

	public static MapResponse fromEntity(Store store) {
		return MapResponse.builder()
			.storeId(store.getStoreId())
			.name(store.getName())
			.latitude(store.getLatitude())
			.longitude(store.getLongitude())
			.likeCount(store.getLikeCount())
			.sigun(store.getSigun())
			.address(store.getAddress())
			.industryCode(store.getIndustryCode())
			.build();
	}
}









