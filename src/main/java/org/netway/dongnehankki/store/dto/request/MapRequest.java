package org.netway.dongnehankki.store.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MapRequest {
	private Double latitude;
	private Double longitude;
	private Integer zoomLevel;
}
