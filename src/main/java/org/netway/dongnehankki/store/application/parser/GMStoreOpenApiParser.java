package org.netway.dongnehankki.store.application.parser;

import java.io.IOException;
import java.util.List;

import org.netway.dongnehankki.store.dto.response.GMStoreOpenApiResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GMStoreOpenApiParser {
	private final ObjectMapper objectMapper;

	public GMStoreOpenApiResponse parse(String json) throws IOException {
		return objectMapper.readValue(json, GMStoreOpenApiResponse.class);
	}

	public int extractTotalCount(GMStoreOpenApiResponse response) {
		return response.getRegionMnyFacltStus().get(0).getHead().get(0).getListTotalCount();
	}

	public List<GMStoreOpenApiResponse.Row> extractRows(GMStoreOpenApiResponse response) {
		return response.getRegionMnyFacltStus().get(1).getRow();
	}

	public boolean isSuccess(GMStoreOpenApiResponse response) {
		return "INFO-000".equals(response.getRegionMnyFacltStus().get(0).getHead().get(1).getResult().getCode());
	}
}
