package org.netway.dongnehankki.store.application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MapBoundingBox {
	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
}
