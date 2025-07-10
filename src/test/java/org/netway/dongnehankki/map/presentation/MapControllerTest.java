package org.netway.dongnehankki.map.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.netway.dongnehankki.map.application.MapService;
import org.netway.dongnehankki.map.presentation.MapController;
import org.netway.dongnehankki.map.dto.request.MapRequest;
import org.netway.dongnehankki.map.dto.response.MapResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(MapController.class)
@WithMockUser
public class MapControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MapService mapService;

	@DisplayName("POST /api/map 요청 시, MapRequest body 없으면 400 Bad Request를 반환")
	@Test
	void getStoresOnMap_noRequestBody_returnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/map")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").exists());
	}

	@DisplayName("POST /api/map 요청 시, MapService를 호출하고 결과를 반환")
	@Test
	void getStoresOnMap_callsMapServiceAndReturnsResult() throws Exception {
		MapRequest requestBody = MapRequest.builder().latitude(37.5665).longitude(126.9780).zoomLevel(3).build();

		MapResponse store1 = MapResponse.builder().storeId(1L).name("Store 1").latitude(37.5).longitude(126.9).build();
		MapResponse store2 = MapResponse.builder().storeId(2L).name("Store 2").latitude(37.6).longitude(127.0).build();
		List<MapResponse> expectedResponse = Arrays.asList(store1, store2);

		when(mapService.getStoresOnMap(ArgumentMatchers.any(MapRequest.class)))
			.thenReturn(expectedResponse);

		mockMvc.perform(post("/api/map")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestBody))
				.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("OK"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isNotEmpty())
			.andExpect(jsonPath("$.data.[0].storeId").value(1))
			.andExpect(jsonPath("$.data.[0].name").value("Store 1"))
			.andExpect(jsonPath("$.data.[1].storeId").value(2))
			.andExpect(jsonPath("$.data.[1].name").value("Store 2"));

		verify(mapService, times(1)).getStoresOnMap(eq(requestBody));
	}

	@DisplayName("POST /api/map 요청 시, MapRequest에 줌 레벨 범위가 다를 때 성공")
	@Test
	void getStoresOnMap_requestBodyNoZoomLevel_usesDefaultValue() throws Exception {
		MapRequest requestBody = MapRequest.builder().latitude(37.5665).longitude(126.9780).zoomLevel(0).build();

		List<MapResponse> expectedStores = Collections.emptyList();

		when(mapService.getStoresOnMap(any(MapRequest.class)))
			.thenReturn(expectedStores);

		mockMvc.perform(post("/api/map")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestBody))
				.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isEmpty());
	}
}