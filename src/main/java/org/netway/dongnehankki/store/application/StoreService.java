package org.netway.dongnehankki.store.application;

import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.request.StoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
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
	public void writeStoreReview(Long storeId, StoreReviewRequest storeReviewRequest) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		User user = userRepository.findByLoginId(storeReviewRequest.getUserLoginId()).orElseThrow(() -> new UnregisteredUserException());
		Review review = Review.createReview(
			storeReviewRequest.getContent(), storeReviewRequest.getScope(), user, store
		);

		store.getReviews().add(review);
		storeRepository.save(store);
		reviewRepository.save(review);
	}
}
