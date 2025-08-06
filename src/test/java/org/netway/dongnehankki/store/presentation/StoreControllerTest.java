package org.netway.dongnehankki.store.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.store.application.StoreService;
import org.netway.dongnehankki.store.dto.response.StoreResponse;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StoreController.class)
@WithMockUser
public class StoreControllerTest {

	@Autowired
	private MockMvc mockMvc;

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

}
