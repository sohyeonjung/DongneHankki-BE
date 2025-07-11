package org.netway.dongnehankki.map.presentation;

import java.util.List;

import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.map.application.MapService;
import org.netway.dongnehankki.map.dto.request.MapRequest;
import org.netway.dongnehankki.map.dto.response.MapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MapController {

	private final MapService mapService;

	@PostMapping("/maps")
	public ResponseEntity<ApiResponse<List<MapResponse>>> getStoresOnMap(
		@RequestBody MapRequest mapRequest
	){
		List<MapResponse> stores = mapService.getStoresOnMap(mapRequest);
		return ResponseEntity.ok(ApiResponse.success(stores));

	}
}
