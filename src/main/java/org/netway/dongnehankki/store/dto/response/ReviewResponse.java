package org.netway.dongnehankki.store.dto.response;

import java.time.LocalDateTime;

import org.netway.dongnehankki.store.domain.Review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {
	private Long reviewId;
	private String content;
	private Integer scope;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long userId;
	private String userName;

	public static ReviewResponse fromEntity(Review review) {
		return ReviewResponse.builder()
			.reviewId(review.getReviewId())
			.content(review.getContent())
			.scope(review.getScope())
			.createdAt(review.getCreatedAt())
			.updatedAt(review.getUpdatedAt())
			.userId(review.getUser().getUserId())
			.userName(review.getUser().getName())
			.build();
	}
}
