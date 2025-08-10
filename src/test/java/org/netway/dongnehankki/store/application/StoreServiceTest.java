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
import org.netway.dongnehankki.store.domain.Menu;
import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.StoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.UnregisteredMenuException;
import org.netway.dongnehankki.store.exception.UnregisteredReviewException;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.MenuRepository;
import org.netway.dongnehankki.store.infrastructure.repository.ReviewRepository;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private MenuRepository menuRepository;

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

	@Test
	@DisplayName("writeStoreReview - 유효하지 않은 store, UnregisterException 반환 ")
	void writeStoreReview_nullStoreReturn() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.writeStoreReview(storeId, null));
	}

	@Test
	@DisplayName("writeStoreReview - 유효하지 않은 User, UnregisterException 반환 ")
	void writeStoreReview_nullUserReturn() {
		// Given
		String userLoginId = "id1";
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		StoreReviewRequest storeReviewRequest = StoreReviewRequest.builder().userLoginId("id1").build();
		// When
		when(storeRepository.findById(any())).thenReturn(Optional.of(testStore));
		when(userRepository.findByLoginId(userLoginId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredUserException.class, () -> storeService.writeStoreReview(1L, storeReviewRequest));
	}

	@Test
	@DisplayName("writeStoreReview - 유효한 입력값에서 정상 작동")
	void writeStoreReview_success() {
		// Given
		Long storeId = 1L;
		String userLoginId = "id1";
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		User testUser = User.ofCustomer("1", "1234", "nickname", "name", "010-1234-5468");
		StoreReviewRequest storeReviewRequest = StoreReviewRequest.builder().userLoginId("id1").content("review test").scope(4).build();

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));
		when(userRepository.findByLoginId(userLoginId)).thenReturn(Optional.of(testUser));

		storeService.writeStoreReview(storeId, storeReviewRequest);

		// Then
		verify(storeRepository, times(1)).findById(storeId);
		verify(userRepository, times(1)).findByLoginId(userLoginId);
		verify(reviewRepository, times(1)).save(any(Review.class));
	}

	@Test
	@DisplayName("addStoreMenu - 유효하지 않은 store, UnregisterException 반환 ")
	void addStoreMenu_nullStoreReturn() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.addStoreMenu(storeId, null));
	}

	@Test
	@DisplayName("addStoreMenu - 유효하지 않은 User, UnregisterException 반환 ")
	void addStoreMenu_nullUserReturn() {
		// Given
		String userLoginId = "id1";
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		StoreMenuRequest storeMenuRequest = StoreMenuRequest.builder().userLoginId("id1").name("menu1").description("menu1 descrp").price(10000).build();

		// When
		when(storeRepository.findById(any())).thenReturn(Optional.of(testStore));
		when(userRepository.findByLoginId(userLoginId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredUserException.class, () -> storeService.addStoreMenu(1L, storeMenuRequest));
	}

	@Test
	@DisplayName("addStoreReview - 유효한 입력값에서 정상 작동")
	void addStoreReview_success() {
		// Given
		Long storeId = 1L;
		String userLoginId = "id1";
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		User testUser = User.ofCustomer("1", "1234", "nickname", "name", "010-1234-5468");
		StoreMenuRequest storeMenuRequest = StoreMenuRequest.builder().userLoginId("id1").name("menu1").description("menu1 descrp").price(10000).build();

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));
		when(userRepository.findByLoginId(userLoginId)).thenReturn(Optional.of(testUser));

		storeService.addStoreMenu(storeId, storeMenuRequest);

		// Then
		verify(storeRepository, times(1)).findById(storeId);
		verify(userRepository, times(1)).findByLoginId(userLoginId);
		verify(menuRepository, times(1)).save(any(Menu.class));
	}

	@Test
	@DisplayName("deleteStoreMenu - 유효하지 않은 store, UnregisterException 반환")
	void deleteStoreMenu_nullStoreReturns() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.deleteStoreMenu(storeId, null));
	}

	@Test
	@DisplayName("deleteStoreMenu - 유효하지 않은 menu, UnregisterException 반환")
	void deleteStoreMenu_nullMenuReturns() {
		// Given
		Long storeId = 1L;
		Long menuId = 999L;
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);


		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));

		// Then
		assertThrows(UnregisteredMenuException.class, () -> storeService.deleteStoreMenu(storeId, menuId));
		verify(menuRepository, never()).delete(any(Menu.class));
		verify(storeRepository, never()).save(any(Store.class));
	}

	@Test
	@DisplayName("deleteStoreMenu - 유효한 입력값에서 정상 작동")
	void deleteStoreMenu_success() {
		// Given
		Long storeId = 1L;
		Long deleteMenuId = 1L;
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		Menu testMenu = mock(Menu.class);
		testStore.getMenus().add(testMenu);


		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));
		when(testMenu.getMenuId()).thenReturn(deleteMenuId);
		storeService.deleteStoreMenu(storeId, deleteMenuId);

		// Then
		verify(storeRepository, times(1)).findById(storeId);
		verify(menuRepository, times(1)).delete(any(Menu.class));
		verify(storeRepository, times(1)).save(testStore);
		assertFalse(testStore.getMenus().contains(testMenu));
	}

	@Test
	@DisplayName("deleteStoreReview - 유효하지 않은 store, UnregisterException 반환")
	void deleteStoreReview_nullStoreReturns() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.deleteStoreReview(storeId, null));
	}

	@Test
	@DisplayName("deleteStoreReview - 유효하지 않은 review, UnregisterException 반환")
	void deleteStoreReview_nullReviewReturns() {
		// Given
		Long storeId = 1L;
		Long reviewId = 999L;
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);


		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));

		// Then
		assertThrows(UnregisteredReviewException.class, () -> storeService.deleteStoreReview(storeId, reviewId));
		verify(reviewRepository, never()).delete(any(Review.class));
		verify(storeRepository, never()).save(any(Store.class));
	}

	@Test
	@DisplayName("deleteStoreMenu - 유효한 입력값에서 정상 작동")
	void deleteStoreReview_success() {
		// Given
		Long storeId = 1L;
		Long deleteReviewId = 1L;
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		Review testReview = mock(Review.class);
		testStore.getReviews().add(testReview);


		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));
		when(testReview.getReviewId()).thenReturn(deleteReviewId);
		storeService.deleteStoreReview(storeId, deleteReviewId);

		// Then
		verify(storeRepository, times(1)).findById(storeId);
		verify(reviewRepository, times(1)).delete(any(Review.class));
		verify(storeRepository, times(1)).save(testStore);
		assertFalse(testStore.getReviews().contains(testReview));
	}


}
