package org.netway.dongnehankki.store.application;

import java.util.List;

import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;

public interface StoreGwangmyeongDataService {
	void saveStores(List<StoreOpenApiResponse.Row> apiRows);
}
