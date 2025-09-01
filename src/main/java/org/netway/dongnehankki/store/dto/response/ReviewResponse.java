package org.netway.dongnehankki.store.dto.response;

import java.time.LocalDateTime;

import org.netway.dongnehankki.store.domain.Review;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		// Long userId = null;
		// String userName = "탈퇴한 사용자";
		//
		// try{
		// 	userId = review.getUser().getUserId();
		// 	userName = review.getUser().getNickname();
		// }catch(EntityNotFoundException e){
		// 	log.info("유저 탈퇴 에러:");
		// }

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
