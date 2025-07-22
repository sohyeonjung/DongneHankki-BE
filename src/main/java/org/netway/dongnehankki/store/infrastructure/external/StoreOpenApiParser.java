package org.netway.dongnehankki.store.infrastructure.external;

import java.io.IOException;
import java.util.List;

import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreOpenApiParser {
	private final ObjectMapper objectMapper;

	public StoreOpenApiResponse parse(String json) throws IOException {
		return objectMapper.readValue(json, StoreOpenApiResponse.class);
	}

	public int extractTotalCount(StoreOpenApiResponse response) {
		return response.getRegionMnyFacltStus().get(0).getHead().get(0).getListTotalCount();
	}

	public List<StoreOpenApiResponse.Row> extractRows(StoreOpenApiResponse response) {
		return response.getRegionMnyFacltStus().get(1).getRow();
	}

	public boolean isSuccess(StoreOpenApiResponse response) {
		return "INFO-000".equals(response.getRegionMnyFacltStus().get(0).getHead().get(1).getResult().getCode());
	}
}
