package org.netway.dongnehankki.store.presentation;

import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.store.application.StoreService;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.StoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@GetMapping("/{storeId}")
	public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(
		@PathVariable Long storeId
	){
		StoreResponse store = storeService.getStoreById(storeId);
		return ResponseEntity.ok(ApiResponse.success(store));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<StoreResponse>> getStoreByBusinessNum(
		@RequestParam Long businessNumber
	){
		StoreResponse store = storeService.getStoreByBusinessNum(businessNumber);
		return ResponseEntity.ok(ApiResponse.success(store));
	}

	@PostMapping("/{storeId}/reviews")
	public ResponseEntity<ApiResponse<Void>> writeStoreReview(
		@PathVariable Long storeId,
		@RequestBody @Valid StoreReviewRequest storeReviewRequest
	){
		storeService.writeStoreReview(storeId, storeReviewRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@PostMapping("/{storeId}/menus")
	public ResponseEntity<ApiResponse<Void>> addStoreMenu(
		@PathVariable Long storeId,
		@RequestBody @Valid StoreMenuRequest storeMenuRequest
	){
		storeService.addStoreMenu(storeId, storeMenuRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@DeleteMapping("/{storeId}/menus/{menuId}")
	public ResponseEntity<ApiResponse<Void>> deleteStoreMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId
	){
		storeService.deleteStoreMenu(storeId, menuId);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@DeleteMapping("/{storeId}/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> deleteStoreReview(
		@PathVariable Long storeId,
		@PathVariable Long reviewId
	){
		storeService.deleteStoreReview(storeId, reviewId);
		return ResponseEntity.ok(ApiResponse.success());
	}

}
