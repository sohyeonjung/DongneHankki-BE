package org.netway.dongnehankki.store.presentation;

import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.store.application.StoreService;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.CreateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreOperatingHoursRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "가게", description = "가게 관련 API")
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@Operation(summary = "아이디로 가게 조회", description = "아이디를 통해 가게의 세부 정보를 조회합니다.")
	@GetMapping("/{storeId}")
	public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "가게 ID") @PathVariable Long storeId
	){
		StoreResponse store = storeService.getStoreById(storeId, userDetails.getUser().getUserId());
		return ResponseEntity.ok(ApiResponse.success(store));
	}

	@Operation(summary = "사업자번호로 가게 조회", description = "사업자번호를 통회 가게 정보를 조회합니다.")
	@GetMapping("/byBusinessNumber/{businessNumber}")
	public ResponseEntity<ApiResponse<StoreResponse>> getStoreByBusinessNum(
		@Parameter(description = "조회할 가게 사업자번호") @PathVariable Long businessNumber
	){
		StoreResponse store = storeService.getStoreByBusinessNum(businessNumber);
		return ResponseEntity.ok(ApiResponse.success(store));
	}

	@Operation(summary = "가게 이름으로 검색", description = "가게 이름으로 가게 목록을 검색합니다.")
	@GetMapping("/byName/{name}")
	public ResponseEntity<ApiResponse<List<StoreResponse>>> getStoresByName(
		@Parameter(description = "검색할 가게 이름") @PathVariable String name
	) {
		List<StoreResponse> stores = storeService.getStoresByName(name);
		return ResponseEntity.ok(ApiResponse.success(stores));
	}

	@Operation(summary = "가게 리뷰 작성", description = "가게 리뷰를 작성합니다.")
	@PostMapping("/{storeId}/reviews")
	public ResponseEntity<ApiResponse<Void>> writeStoreReview(
		@Parameter(description = "가게 ID") @PathVariable Long storeId,
		@RequestBody @Valid CreateStoreReviewRequest createStoreReviewRequest
	){
		storeService.writeStoreReview(storeId, createStoreReviewRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Operation(summary = "가게 메뉴 작성", description = "가게의 메뉴를 작성합니다.")
	@PostMapping("/{storeId}/menus")
	public ResponseEntity<ApiResponse<Void>> addStoreMenu(
		@Parameter(description = "가게 ID") @PathVariable Long storeId,
		@RequestBody @Valid StoreMenuRequest storeMenuRequest
	){
		storeService.addStoreMenu(storeId, storeMenuRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Operation(summary = "가게 메뉴 삭제", description = "가게의 메뉴를 삭제합니다.")
	@DeleteMapping("/{storeId}/menus/{menuId}")
	public ResponseEntity<ApiResponse<Void>> deleteStoreMenu(
		@Parameter(description = "가게 ID") @PathVariable Long storeId,
		@Parameter(description = "수정할 메뉴 ID") @PathVariable Long menuId
	){
		storeService.deleteStoreMenu(storeId, menuId);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Operation(summary = "가게 리뷰 삭제", description = "가게의 리뷰를 삭제합니다.")
	@DeleteMapping("/{storeId}/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> deleteStoreReview(
		@Parameter(description = "가게 ID") @PathVariable Long storeId,
		@Parameter(description = "삭제할 리뷰 ID") @PathVariable Long reviewId
	){
		storeService.deleteStoreReview(storeId, reviewId);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Operation(summary = "가게 운영시간 수정", description = "가게의 운영시간을 수정합니다.")
	@PatchMapping("/{storeId}/operatingHours")
	public ResponseEntity<ApiResponse<Void>> updateOperatingHours(
		@Parameter(description = "수정할 가게 ID") @PathVariable Long storeId,
		@RequestBody @Valid UpdateStoreOperatingHoursRequest updateStoreOperatingHoursRequest
	){
		storeService.updateStoreOperatingHours(storeId, updateStoreOperatingHoursRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Operation(summary = "가게 리뷰 수정", description = "가게에 달았던 리뷰를 수정합니다.")
	@PatchMapping("/{storeId}/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> updateStoreReview(
		@Parameter(description = "수정할 가게 ID") @PathVariable Long storeId,
		@Parameter(description = "수정할 리뷰 ID")@PathVariable Long reviewId,
		@RequestBody @Valid UpdateStoreReviewRequest updateStoreReviewRequest
	){
		storeService.updateStoreReview(storeId, reviewId, updateStoreReviewRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}

}
