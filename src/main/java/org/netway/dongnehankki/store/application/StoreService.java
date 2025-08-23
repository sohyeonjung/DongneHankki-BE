package org.netway.dongnehankki.store.application;


import java.util.List;

import org.netway.dongnehankki.store.domain.OperatingHour;
import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.domain.Menu;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.CreateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreOperatingHoursRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.ReviewStoreMismatchException;
import org.netway.dongnehankki.store.exception.UnregisteredMenuException;
import org.netway.dongnehankki.store.exception.UnregisteredReviewException;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.MenuRepository;
import org.netway.dongnehankki.store.infrastructure.repository.ReviewRepository;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final ReviewRepository reviewRepository;
	private final MenuRepository menuRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public StoreResponse getStoreById(Long storeId) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		return StoreResponse.fromEntity(store);
	}

	@Transactional(readOnly = true)
	public StoreResponse getStoreByBusinessNum(Long businessNum) {
		Store store = storeRepository.findByBusinessRegistrationNumber(businessNum).orElseThrow(() -> new UnregisteredStoreException());
		return StoreResponse.fromEntity(store);
	}

	@Transactional
	public void writeStoreReview(Long storeId, CreateStoreReviewRequest createStoreReviewRequest) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		User user = userRepository.findById(createStoreReviewRequest.getUserId()).orElseThrow(() -> new UnregisteredUserException());
		Review review = Review.createReview(
			createStoreReviewRequest.getContent(), createStoreReviewRequest.getScope(), user, store
		);

		store.getReviews().add(review);
		storeRepository.save(store);
		reviewRepository.save(review);
	}

	@Transactional
	public void addStoreMenu(Long storeId, StoreMenuRequest storeMenuRequest) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		User user = userRepository.findById(storeMenuRequest.getUserId()).orElseThrow(() -> new UnregisteredUserException());
		Menu menu = Menu.createMenu(
			storeMenuRequest.getName(), storeMenuRequest.getDescription(), storeMenuRequest.getImage(), storeMenuRequest.getPrice(), store, user
		);

		store.getMenus().add(menu);
		storeRepository.save(store);
		menuRepository.save(menu);
	}

	@Transactional
	public void deleteStoreMenu(Long storeId, Long menuId) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		Menu menuToDelete = store.getMenus().stream()
			.filter(it->it.getMenuId().equals(menuId))
			.findFirst().orElseThrow(() -> new UnregisteredMenuException());

		store.getMenus().remove(menuToDelete);
		menuRepository.delete(menuToDelete);
		storeRepository.save(store);
	}

	@Transactional
	public void deleteStoreReview(Long storeId, Long reviewId) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		Review reviewToDelete = store.getReviews().stream()
			.filter(it->it.getReviewId().equals(reviewId))
			.findFirst().orElseThrow(() -> new UnregisteredReviewException());

		store.getReviews().remove(reviewToDelete);
		reviewRepository.delete(reviewToDelete);
		storeRepository.save(store);
	}

	@Transactional
	public void updateStoreReview(Long storeId, Long reviewId, UpdateStoreReviewRequest updateStoreReviewRequest) {
		storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new UnregisteredReviewException());
		if(!review.getStore().getStoreId().equals(storeId)) throw new ReviewStoreMismatchException();

		review.updateReview(updateStoreReviewRequest.getContent(), updateStoreReviewRequest.getScope());

		reviewRepository.save(review);
	}

	@Transactional
	public void updateStoreOperatingHours(Long storeId, UpdateStoreOperatingHoursRequest updateStoreOperatingHoursRequest) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());

		List<OperatingHour> operatingHours = updateStoreOperatingHoursRequest.getOperatingHours().stream()
			.map(it->OperatingHour.createOperatingHour(it.getDayOfWeek(), it.getOpenTime(), it.getCloseTime())).toList();

		store.updateOperatingHours(operatingHours);
		storeRepository.save(store);
	}
}
