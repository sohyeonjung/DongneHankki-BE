package org.netway.dongnehankki.store.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.store.application.StoreService;
import org.netway.dongnehankki.store.dto.request.StoreMenuRequest;
import org.netway.dongnehankki.store.dto.request.CreateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreOperatingHoursRequest;
import org.netway.dongnehankki.store.dto.request.UpdateStoreReviewRequest;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.ReviewStoreMismatchException;
import org.netway.dongnehankki.store.exception.UnregisteredMenuException;
import org.netway.dongnehankki.store.exception.UnregisteredReviewException;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(StoreController.class)
@WithMockUser
public class StoreControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private StoreService storeService;

	@DisplayName("GET /stores/{storeId} - 존재하지 않는 id 에러 반환")
	@Test
	void getStoreById_NotFound() throws Exception {
		//given
		Long notExistStoreId = 999L;

		//when
		when(storeService.getStoreById(notExistStoreId)).thenThrow(new UnregisteredStoreException());

		//then
		mockMvc.perform(get("/api/stores/{storeId}", notExistStoreId))
			.andExpect(status().isNotFound());
	}

	@DisplayName("GET /stores/{storeId} - 존재하는 id 결과를 반환")
	@Test
	void getStoreById_Success() throws Exception {
		//given
		StoreResponse store = StoreResponse.builder().storeId(1L).name("store a").sigun("광명시").build();
		Long storeId = 1L;

		//when
		when(storeService.getStoreById(storeId)).thenReturn(store);

		//then
		mockMvc.perform(get("/api/stores/{storeId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("OK"))
			.andExpect(jsonPath("$.data").isNotEmpty())
			.andExpect(jsonPath("$.data.storeId").value(storeId))
			.andExpect(jsonPath("$.data.name").value("store a"))
			.andExpect(jsonPath("$.data.sigun").value("광명시"));

		verify(storeService, times(1)).getStoreById(storeId);
	}

	@DisplayName("GET /stores?businessNum={businessNum} - 존재하지 않는 businessNum 에러 반환")
	@Test
	void getStoreByBusinessNum_NotFound() throws Exception {
		//given
		Long notExistBusinessNum = 999L;

		//when
		when(storeService.getStoreByBusinessNum(notExistBusinessNum)).thenThrow(new UnregisteredStoreException());

		//then
		mockMvc.perform(get("/api/stores").param("businessNumber", String.valueOf(notExistBusinessNum)))
			.andExpect(status().isNotFound());
	}

	@DisplayName("GET /stores?businessNum={businessNum}  - 존재하는 businessNum 결과를 반환")
	@Test
	void getStoreByBusinesNum_Success() throws Exception {
		//given
		StoreResponse store = StoreResponse.builder().storeId(1L).name("store a").sigun("광명시").businessRegistrationNumber(1234567890L).build();
		Long businessNum = 1234567890L;

		//when
		when(storeService.getStoreByBusinessNum(businessNum)).thenReturn(store);

		//then
		mockMvc.perform(get("/api/stores").param("businessNumber", String.valueOf(businessNum))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("OK"))
			.andExpect(jsonPath("$.data").isNotEmpty())
			.andExpect(jsonPath("$.data.businessRegistrationNumber").value(businessNum))
			.andExpect(jsonPath("$.data.name").value("store a"))
			.andExpect(jsonPath("$.data.sigun").value("광명시"));

		verify(storeService, times(1)).getStoreByBusinessNum(businessNum);
	}

	@Test
	@DisplayName("유효하지 않은 리뷰 요청시 400 Bad Request를 반환해야 한다")
	void writeStoreReview_InvalidRequest_BadRequest() throws Exception {
		// given
		CreateStoreReviewRequest invalidReviewRequest = CreateStoreReviewRequest.builder().userLoginId("id2")
				.content("Bad content").scope(6).build();

		// then
		mockMvc.perform(post("/api/stores/{storeId}/reviews", 1L)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidReviewRequest)))
			.andExpect(status().isBadRequest());

		verify(storeService, never()).writeStoreReview(anyLong(), any(CreateStoreReviewRequest.class));
	}


	@DisplayName("POST /stores/{storeId}/reviews - 유효한 요청 시 200 반환")
	@Test
	void writeStoreReview_Success() throws Exception {
		//given
		CreateStoreReviewRequest validReviewRequest = CreateStoreReviewRequest.builder().userLoginId("id1").content("review").scope(4).build();

		//when
		doNothing().when(storeService).writeStoreReview(anyLong(), any(CreateStoreReviewRequest.class));

		//then
		mockMvc.perform(post("/api/stores/{storeId}/reviews", 1L)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validReviewRequest)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST /stores/{storeId}/menus - 유효하지 않은 요청시 400 Bad Request를 반환")
	void addStoreMenu_InvalidRequest_BadRequest() throws Exception {
		// given
		StoreMenuRequest invalidMenuRequest = StoreMenuRequest.builder().userLoginId("id1").name("menu1").description("menu1 descrp").build();

		// then
		mockMvc.perform(post("/api/stores/{storeId}/menus", 1L)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidMenuRequest)))
			.andExpect(status().isBadRequest());

		verify(storeService, never()).writeStoreReview(anyLong(), any(CreateStoreReviewRequest.class));
	}

	@DisplayName("POST /stores/{storeId}/menus - 유효한 요청 시 200 반환")
	@Test
	void addStoreMenu_Success() throws Exception {
		//given
		StoreMenuRequest validMenuRequest = StoreMenuRequest.builder().userLoginId("id1").name("menu1").description("menu1 descrp").image("link").price(10000).build();

		//when
		doNothing().when(storeService).writeStoreReview(anyLong(), any(CreateStoreReviewRequest.class));

		//then
		mockMvc.perform(post("/api/stores/{storeId}/menus", 1L)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validMenuRequest)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("DELETE /stores/{storeId}/menus/{menuId} - 상점 ID가 없을 경우 404 Not Found 반환")
	void deleteStoreMenu_StoreNotFound() throws Exception {
		// Given
		Long menuId = 1L;
		doThrow(new UnregisteredStoreException()).when(storeService).deleteStoreMenu(anyLong(), anyLong());

		// Then
		mockMvc.perform(delete("/api/stores/{storeId}/menus/{menuId}", 99L, menuId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		verify(storeService, times(1)).deleteStoreMenu(eq(99L), eq(menuId));
	}

	@Test
	@DisplayName("DELETE /stores/{storeId}/menus/{menuId} - 메뉴 ID가 없을 경우 404 Not Found 반환")
	void deleteStoreMenu_MenuNotFound() throws Exception {
		// Given
		Long storeId = 1L;
		doThrow(new UnregisteredMenuException()).when(storeService).deleteStoreMenu(anyLong(), anyLong());

		// Then
		mockMvc.perform(delete("/api/stores/{storeId}/menus/{menuId}", storeId, 999L)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		verify(storeService, times(1)).deleteStoreMenu(eq(storeId), eq(999L));
	}

	@DisplayName("DELETE /stores/{storeId}/menus/{menuId} - 유효한 요청 시 200 반환")
	@Test
	void deleteStoreMenu_Success() throws Exception {
		//given
		Long storeId = 1L;
		Long menuId = 2L;

		//when
		doNothing().when(storeService).deleteStoreMenu(storeId, menuId);

		//then
		mockMvc.perform(delete("/api/stores/{storeId}/menus/{menuId}", storeId, menuId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		verify(storeService, times(1)).deleteStoreMenu(eq(storeId), eq(menuId));
	}

	@Test
	@DisplayName("DELETE /stores/{storeId}/reviews/{reviewId}- 상점 ID가 없을 경우 404 Not Found 반환")
	void deleteStoreReview_StoreNotFound() throws Exception {
		// Given
		Long reviewId = 1L;
		doThrow(new UnregisteredStoreException()).when(storeService).deleteStoreReview(anyLong(), anyLong());

		// Then
		mockMvc.perform(delete("/api/stores/{storeId}/reviews/{reviewId}", 99L, reviewId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		verify(storeService, times(1)).deleteStoreReview(eq(99L), eq(reviewId));
	}

	@Test
	@DisplayName("DELETE /stores/{storeId}/reviews/{reviewId} - 리뷰 ID가 없을 경우 404 Not Found 반환")
	void deleteStoreReview_ReviewNotFound() throws Exception {
		// Given
		Long storeId = 1L;
		doThrow(new UnregisteredReviewException()).when(storeService).deleteStoreReview(anyLong(), anyLong());

		// Then
		mockMvc.perform(delete("/api/stores/{storeId}/reviews/{reviewId}", storeId, 999L)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		verify(storeService, times(1)).deleteStoreReview(eq(storeId), eq(999L));
	}

	@DisplayName("DELETE /stores/{storeId}/reviews/{reviewId} - 유효한 요청 시 200 반환")
	@Test
	void deleteStoreReview_Success() throws Exception {
		//given
		Long storeId = 1L;
		Long reviewId = 2L;

		//when
		doNothing().when(storeService).deleteStoreReview(storeId, reviewId);

		//then
		mockMvc.perform(delete("/api/stores/{storeId}/reviews/{reviewId}", storeId, reviewId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		verify(storeService, times(1)).deleteStoreReview(eq(storeId), eq(reviewId));
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/operatingHours - 유효하지 않은 요청 시 400 Bad Request 반환 (리스트 길이가 7이 아닌 경우)")
	void updateOperatingHours_BadRequest() throws Exception {
		// Given
		Long storeId = 1L;
		UpdateStoreOperatingHoursRequest invalidRequest = UpdateStoreOperatingHoursRequest.builder()
			.operatingHours(List.of(
				UpdateStoreOperatingHoursRequest.OperatingHourRequest.builder().dayOfWeek(DayOfWeek.MONDAY).openTime(
					LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).build()
			))
			.build();

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/operatingHours", storeId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
			.andExpect(status().isBadRequest());

		verify(storeService, never()).updateStoreOperatingHours(anyLong(), any(UpdateStoreOperatingHoursRequest.class));
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/operatingHours - 상점 ID가 없을 경우 404 Not Found 반환")
	void updateOperatingHours_StoreNotFound()  throws Exception {
		// Given
		Long storeId = 999L;
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
		UpdateStoreOperatingHoursRequest validRequest = UpdateStoreOperatingHoursRequest.builder().operatingHours(operatingHourRequests).build();

		// When
		doThrow(new UnregisteredStoreException()).when(storeService).updateStoreOperatingHours(eq(storeId), any(UpdateStoreOperatingHoursRequest.class));

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/operatingHours", storeId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest)))
			.andExpect(status().isNotFound());

		verify(storeService, times(1)).updateStoreOperatingHours(eq(storeId), any(UpdateStoreOperatingHoursRequest.class));
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/operatingHours - 유효한 요청 시 200 OK 반환")
	void updateOperatingHours_Success() throws Exception {
		// Given
		Long storeId = 1L;
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
		UpdateStoreOperatingHoursRequest validRequest = UpdateStoreOperatingHoursRequest.builder().operatingHours(operatingHourRequests).build();

		// When
		doNothing().when(storeService).updateStoreOperatingHours(eq(storeId), any(UpdateStoreOperatingHoursRequest.class));

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/operatingHours", storeId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("OK"));

		verify(storeService, times(1)).updateStoreOperatingHours(eq(storeId), any(UpdateStoreOperatingHoursRequest.class));
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/reviews/{reviewId} - 유효하지 않은 요청 시 400 Bad Request 반환")
	void updateStoreReview_BadRequest() throws Exception {
		// Given
		Long storeId = 1L;
		Long reviewId = 2L;
		UpdateStoreReviewRequest invalidRequest = UpdateStoreReviewRequest.builder().content("invalide").build();

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/reviews/{reviewId}", storeId, reviewId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
			.andExpect(status().isBadRequest());

		verify(storeService, never()).updateStoreOperatingHours(anyLong(), any(UpdateStoreOperatingHoursRequest.class));
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/reviews/{reviewId} - 상점 ID가 없을 경우 404 Not Found 반환")
	void updateStoreReview_StoreNotFound() throws Exception {
		// Given
		Long storeId = 1L;
		Long reviewId = 2L;
		UpdateStoreReviewRequest validRequest = UpdateStoreReviewRequest.builder().content("invalide").scope(4).build();

		//when
		doThrow(new UnregisteredStoreException()).when(storeService).updateStoreReview(eq(storeId), eq(reviewId), any(UpdateStoreReviewRequest.class));

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/reviews/{reviewId}", storeId, reviewId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/reviews/{reviewId} - 상점일 일치 하지 않을 경우 400 Bad Request 반환")
	void updateStoreReview_StoreMisMatch() throws Exception {
		// Given
		Long storeId = 1L;
		Long reviewId = 2L;
		UpdateStoreReviewRequest validRequest = UpdateStoreReviewRequest.builder().content("invalide").scope(4).build();

		//when
		doThrow(new ReviewStoreMismatchException()).when(storeService).updateStoreReview(eq(storeId), eq(reviewId), any(UpdateStoreReviewRequest.class));

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/reviews/{reviewId}", storeId, reviewId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PATCH /stores/{storeId}/reviews/{reviewId} - 유효한 요청 시 200 OK 반환 ")
	void updateStoreReview_Success() throws Exception {
		// Given
		Long storeId = 1L;
		Long reviewId = 2L;
		UpdateStoreReviewRequest validRequest = UpdateStoreReviewRequest.builder().content("invalide").scope(4).build();

		// Then
		mockMvc.perform(patch("/api/stores/{storeId}/reviews/{reviewId}", storeId, reviewId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("OK"));
	}
}
