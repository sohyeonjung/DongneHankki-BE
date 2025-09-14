package org.netway.dongnehankki.map.application;

import java.util.List;

import org.netway.dongnehankki.map.dto.request.MapRequest;
import org.netway.dongnehankki.map.dto.response.MapResponse;

public interface MapService {
	List<MapResponse> getStoresOnMap(MapRequest mapRequest);
}
