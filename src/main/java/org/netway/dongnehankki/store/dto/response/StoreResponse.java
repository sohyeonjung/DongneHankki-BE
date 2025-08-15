package org.netway.dongnehankki.store.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.netway.dongnehankki.map.dto.response.OperatingHourResponse;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.user.dto.response.UserResponse;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoreResponse {
	private Long storeId;
	private String name;
	private Double latitude;
	private Double longitude;
	private Integer likeCount;
	private String sigun;
	private String address;
	private Integer industryCode;
	private Long businessRegistrationNumber;
	private Double avgStar;
	private List<OperatingHourResponse> operatingHours;

	private UserResponse owner;
	private List<MenuResponse> menus;
	private List<ReviewResponse> reviews;

	// TODO
	// private List<PostResponse> posts = new ArrayList<>();
	// private List<FollowResponse> follows = new ArrayList<>();

	public static StoreResponse fromEntity(Store store) {
		UserResponse user = store.getUser() != null ? UserResponse.fromEntity(store.getUser()) : null;
		List<MenuResponse> menus = store.getMenus().stream().map(MenuResponse::fromEntity).toList();
		List<ReviewResponse> reviews = store.getReviews().stream().map(ReviewResponse::fromEntity).toList();
		List<OperatingHourResponse> operatingHours = store.getOperatingHours()
			.stream().map(OperatingHourResponse::fromEntity).toList();

		return StoreResponse.builder()
			.storeId(store.getStoreId())
			.name(store.getName())
			.latitude(store.getLatitude())
			.longitude(store.getLongitude())
			.sigun(store.getSigun())
			.address(store.getAddress())
			.industryCode(store.getIndustryCode())
			.businessRegistrationNumber(store.getBusinessRegistrationNumber())
			.avgStar(store.getAverageStar())
			.operatingHours(operatingHours)
			.owner(user)
			.menus(menus)
			.reviews(reviews)
			.build();
	}
}
