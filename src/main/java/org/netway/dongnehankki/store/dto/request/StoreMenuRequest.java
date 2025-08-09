package org.netway.dongnehankki.store.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreMenuRequest {
	@NotEmpty
	private String userLoginId;
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	private String image;
	@NotNull
	private Integer price;
}
