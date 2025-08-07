package org.netway.dongnehankki.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreReviewRequest {
	@NotEmpty
	String userLoginId;
	@NotNull
	String content;
	@Min(1)
	@Max(5)
	Integer scope;
}
