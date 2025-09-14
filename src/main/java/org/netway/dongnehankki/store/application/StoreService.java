package org.netway.dongnehankki.store.application;

import java.util.List;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.CreateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreOperatingHoursRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;

public interface StoreService {
	StoreResponse getStoreById(Long storeId, Long loginId);
	StoreResponse getStoreByBusinessNum(Long businessNum);
	void writeStoreReview(Long storeId, CreateStoreReviewRequest createStoreReviewRequest);
	void addStoreMenu(Long storeId, StoreMenuRequest storeMenuRequest);
	void deleteStoreMenu(Long storeId, Long menuId);
	void deleteStoreReview(Long storeId, Long reviewId);
	void updateStoreReview(Long storeId, Long reviewId, UpdateStoreReviewRequest updateStoreReviewRequest);
	void updateStoreOperatingHours(Long storeId, UpdateStoreOperatingHoursRequest updateStoreOperatingHoursRequest);
	List<StoreResponse> searchStoresByName(String name);
}
