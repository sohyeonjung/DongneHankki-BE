package org.netway.dongnehankki.store.application;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;

	public StoreResponse getStore(Long storeId) {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new UnregisteredStoreException());
		return StoreResponse.fromEntity(store);
	}

	public StoreResponse getStoreByBusinessNum(Long businessNum) {
		Store store = storeRepository.findByBusinessRegistrationNumber(businessNum).orElseThrow(() -> new UnregisteredStoreException());
		return StoreResponse.fromEntity(store);
	}

}
