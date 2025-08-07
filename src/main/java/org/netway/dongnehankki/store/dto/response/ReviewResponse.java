package org.netway.dongnehankki.store.dto.response;

import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.user.dto.response.UserResponse;

import lombok.Builder;

@Builder
public class ReviewResponse {
	private Long reviewId;
	private String content;
	private Integer scope;
	private UserResponse reviewUser;

	public static ReviewResponse fromEntity(Review review) {
		UserResponse user = UserResponse.fromEntity(review.getUser());

		return ReviewResponse.builder()
			.reviewId(review.getReviewId())
			.content(review.getContent())
			.scope(review.getScope())
			.reviewUser(user)
			.build();
	}
}
