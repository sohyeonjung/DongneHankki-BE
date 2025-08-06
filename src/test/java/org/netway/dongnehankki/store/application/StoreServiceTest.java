package org.netway.dongnehankki.store.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private StoreService storeService;

	@Test
	@DisplayName("getStoreById - 해당하는 store가 없을시 UnregisterException 반환")
	void testGetStore_nullReturn() {
		//Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.getStoreById(storeId));
	}

	@Test
	@DisplayName("getStore - 해당하는 store가 있을 시 잘 반환")
	void testGetStoreById_success() {
		//Given
		Store store = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		StoreResponse result = storeService.getStoreById(storeId);

		// Then
		assertThat(result.getName()).isEqualTo("storeA");
		assertThat(result.getAddress()).isEqualTo("경기도 광명시 A");
		assertThat(result.getIndustryCode()).isEqualTo(2316);
	}

	@Test
	@DisplayName("getStoreByBusinessNum - 해당하는 store가 없을시 UnregisterException 반환")
	void testGetStoreByBusinessNum_nullReturn() {
		//Given
		Long businessNum = 1L;

		// When
		when(storeRepository.findByBusinessRegistrationNumber(businessNum)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.getStoreByBusinessNum(businessNum));
	}

	@Test
	@DisplayName("getStoreByBusinessNum - 해당하는 store가 있을 시 잘 반환")
	void testGetStoreByBusinessNum_success() {
		//Given
		Store store = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		Long businessNum = 12356102561L;

		// When
		when(storeRepository.findByBusinessRegistrationNumber(businessNum)).thenReturn(Optional.of(store));
		StoreResponse result = storeService.getStoreByBusinessNum(businessNum);

		// Then
		assertThat(result.getName()).isEqualTo("storeA");
		assertThat(result.getAddress()).isEqualTo("경기도 광명시 A");
		assertThat(result.getIndustryCode()).isEqualTo(2316);
	}

}
