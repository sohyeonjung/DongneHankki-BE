package org.netway.dongnehankki.store.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.store.domain.Menu;
import org.netway.dongnehankki.store.domain.OperatingHour;
import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.CreateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.request.StoreStarRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreOperatingHoursRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.ReviewStoreMismatchException;
import org.netway.dongnehankki.store.exception.UnregisterdStarException;
import org.netway.dongnehankki.store.exception.UnregisteredMenuException;
import org.netway.dongnehankki.store.exception.UnregisteredReviewException;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.MenuRepository;
import org.netway.dongnehankki.store.infrastructure.repository.ReviewRepository;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.test.util.ReflectionTestUtils;

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
		CreateStoreReviewRequest createStoreReviewRequest = CreateStoreReviewRequest.builder().userLoginId("id1").build();
		// When
		when(storeRepository.findById(any())).thenReturn(Optional.of(testStore));
		when(userRepository.findByLoginId(userLoginId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredUserException.class, () -> storeService.writeStoreReview(1L, createStoreReviewRequest));
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
		CreateStoreReviewRequest createStoreReviewRequest = CreateStoreReviewRequest.builder().userLoginId("id1").content("review test").scope(4).build();

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));
		when(userRepository.findByLoginId(userLoginId)).thenReturn(Optional.of(testUser));

		storeService.writeStoreReview(storeId, createStoreReviewRequest);

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
	@DisplayName("addStoreStar - 유효하지 않은 store, UnregisterException 반환 ")
	void addStoreStar_nullStoreReturn() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.addStoreStar(storeId, null));
	}

	@Test
	@DisplayName("addStoreStar - 유효한 입력 값에서 정상 작동")
	void addStoreStar_Success() {
		// Given
		Long storeId = 1L;
		Long userId = 10L;
		StoreStarRequest request = StoreStarRequest.builder().userId(userId).star(5).build();

		Store store = new Store();

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		storeService.addStoreStar(storeId, request);

		// Then
		assertThat(store.getStars().get(userId)).isEqualTo(5);
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

	@Test
	@DisplayName("deleteStoreStar - 유효하지 않은 store, UnregisterException 반환")
	void deleteStoreStar_nullStoreReturns() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.deleteStoreStar(storeId, null));
	}

	@Test
	@DisplayName("deleteStoreStar - 유효하지 않은 star, UnregisterException 반환")
	void deleteStoreStar_nullStarReturns() {
		// Given
		Long storeId = 1L;
		Store testStore = mock(Store.class);
		Long userId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.ofNullable(testStore));

		// Then
		assertThrows(UnregisterdStarException.class, () -> storeService.deleteStoreStar(storeId, userId));
	}

	@Test
	@DisplayName("deleteStoreStar - 유효한 입력값에서 정상 작동")
	void deleteStoreStar_Success() {
		// Given
		Long storeId = 1L;
		Long userId = 1L;
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		testStore.getStars().put(userId, 5);

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.ofNullable(testStore));
		storeService.deleteStoreStar(storeId, userId);

		// Then
		verify(storeRepository, times(1)).save(testStore);
		assertFalse(testStore.getStars().containsKey(userId));
	}

	@Test
	@DisplayName("updateStoreOperationgHours - 유효하지 않은 store, UnregisterException 반환")
	void updateStoreOperatingHours_nullStoreReturns() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.updateStoreOperatingHours(storeId, null));
	}

	@Test
	@DisplayName("updateStoreOperationgHours - 유효한 입력 값에서 정상 작동")
	void updateStoreOperatingHours_success() {
		// Given
		Long storeId = 1L;
		Store testStore = mock(Store.class);
		List<UpdateStoreOperatingHoursRequest.OperatingHourRequest> operatingHourRequests = List.of(
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.MONDAY).openTime(
				LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).build(),
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.TUESDAY).openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).build(),
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.WEDNESDAY).openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).build(),
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.THURSDAY).openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).build(),
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.FRIDAY).openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).build(),
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.SATURDAY).openTime(LocalTime.of(10, 0)).closeTime(LocalTime.of(17, 0)).build(),
			UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.SUNDAY).openTime(LocalTime.of(10, 0)).closeTime(LocalTime.of(17, 0)).build()
		);
		UpdateStoreOperatingHoursRequest updateStoreOperatingHoursRequest = UpdateStoreOperatingHoursRequest.builder().operatingHours(operatingHourRequests).build();

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(testStore));
		storeService.updateStoreOperatingHours(storeId, updateStoreOperatingHoursRequest);

		// Then
		verify(storeRepository, times(1)).findById(storeId);
		verify(testStore, times(1)).updateOperatingHours(any(List.class));

		ArgumentCaptor<List<OperatingHour>> captor = ArgumentCaptor.forClass(List.class);
		verify(testStore, times(1)).updateOperatingHours(captor.capture());

		List<OperatingHour> capturedHours = captor.getValue();
		assertThat(capturedHours.size()).isEqualTo(7);

		OperatingHour mondayHour = capturedHours.get(0);
		assertThat(mondayHour.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
		assertThat(mondayHour.getOpenTime()).isEqualTo(LocalTime.of(9, 0));
		assertThat(mondayHour.getCloseTime()).isEqualTo(LocalTime.of(18, 0));

		verify(storeRepository, times(1)).save(testStore);
	}

	@Test
	@DisplayName("updateStoreReview - 유효하지 않은 store, UnregisterException 반환")
	void updateStoreReview_nullStoreReturns() {
		// Given
		Long storeId = 1L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredStoreException.class, () -> storeService.updateStoreReview(storeId, null, null));
	}

	@Test
	@DisplayName("updateStoreReview - 유효하지 않은 review, UnregisterException 반환")
	void updateStoreReview_nullReviewReturns() {
		// Given
		Long storeId = 1L;
		Store testStore = mock(Store.class);
		Long reviewId = 2L;

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.ofNullable(testStore));
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// Then
		assertThrows(UnregisteredReviewException.class, () -> storeService.updateStoreReview(storeId, reviewId, null));
	}

	@Test
	@DisplayName("updateStoreReview - 유효하지 않은 store, review 시 MisMatchException 반환")
	void updateStoreReview_mismatchStoreReturns() {
		// Given
		Long storeId = 1L;
		Long reviewId = 2L;
		Store testStore = mock(Store.class);
		Store invalidStore = mock(Store.class);
		Review testReview = mock(Review.class);

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.ofNullable(testStore));
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.ofNullable(testReview));
		when(testReview.getStore()).thenReturn(invalidStore);

		// Then
		assertThrows(ReviewStoreMismatchException.class, () -> storeService.updateStoreReview(storeId, reviewId, null));
	}

	@Test
	@DisplayName("updateStoreReview - 유효한 입력 값에서 정상 작동")
	void updateStoreReview_success() {
		// Given
		Long storeId = 1L;
		Long reviewId = 2L;
		Store testStore = Store.createStore("storeA", 127.1535, 52.123, "경기도 광명시 A",
			"광명시", 2316, 12356102561L);
		Review testReview = Review.createReview("review1", 5, null, testStore);
		testStore.getReviews().add(testReview);
		UpdateStoreReviewRequest updateStoreReviewRequest = UpdateStoreReviewRequest.builder().content("review2").scope(4).build();

		ReflectionTestUtils.setField(testStore, "storeId", storeId);
		ReflectionTestUtils.setField(testReview, "reviewId", reviewId);

		// When
		when(storeRepository.findById(storeId)).thenReturn(Optional.ofNullable(testStore));
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.ofNullable(testReview));
		storeService.updateStoreReview(storeId, reviewId, updateStoreReviewRequest);

		// Then
		verify(reviewRepository, times(1)).save(testReview);
		assertThat(testStore.getReviews().get(0).getContent()).isEqualTo("review2");
		assertThat(testStore.getReviews().get(0).getScope()).isEqualTo(4);
	}
}
