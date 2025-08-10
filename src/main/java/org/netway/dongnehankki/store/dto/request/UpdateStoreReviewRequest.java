package org.netway.dongnehankki.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateStoreReviewRequest {
	@NotNull
	String content;
	@NotNull
	@Min(1)
	@Max(5)
	Integer scope;
}
