package org.netway.dongnehankki.map.presentation;

import java.util.List;

import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.map.application.MapService;
import org.netway.dongnehankki.map.dto.request.MapRequest;
import org.netway.dongnehankki.map.dto.response.MapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "지도", description = "지도 가게 조회 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MapController {

	private final MapService mapService;

	@Operation(summary = "지도 가게 조회", description = "지도에서 위치에 해당하는 가게를 조회합니다.")
	@GetMapping("/maps")
	public ResponseEntity<ApiResponse<List<MapResponse>>> getStoresOnMap(
		@ModelAttribute @Valid MapRequest mapRequest
	){
		List<MapResponse> stores = mapService.getStoresOnMap(mapRequest);
		return ResponseEntity.ok(ApiResponse.success(stores));
	}
}
