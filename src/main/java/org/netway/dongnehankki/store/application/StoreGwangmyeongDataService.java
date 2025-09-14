package org.netway.dongnehankki.store.application;

import java.util.List;

import org.netway.dongnehankki.store.dto.response.GMStoreOpenApiResponse;

public interface StoreGwangmyeongDataService {
	void saveStores(List<GMStoreOpenApiResponse.Row> apiRows);
	void saveAllStores(int pageSize);
}
