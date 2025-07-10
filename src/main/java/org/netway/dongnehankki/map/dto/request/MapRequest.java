package org.netway.dongnehankki.map.dto.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class MapRequest {
	private Double latitude;
	private Double longitude;
	private Integer zoomLevel;
}
