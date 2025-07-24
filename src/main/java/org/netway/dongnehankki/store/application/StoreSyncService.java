package org.netway.dongnehankki.store.application;

import java.util.List;

import org.netway.dongnehankki.store.exception.OpenApiException;
import org.netway.dongnehankki.store.infrastructure.external.StoreOpenApiClient;
import org.netway.dongnehankki.store.infrastructure.external.StoreOpenApiParser;
import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreSyncService {

	private final StoreOpenApiClient storeOpenApiClient;
	private final StoreOpenApiParser storeOpenApiParser;
	private final StoreDataService storeDataService;

	private static final int PAGE_SIZE = 1000;

	public void sync() {
		try {
			String firstPageJson = storeOpenApiClient.fetchStoreData(1, PAGE_SIZE);
			StoreOpenApiResponse firstResponse = storeOpenApiParser.parse(firstPageJson);
			if (!storeOpenApiParser.isSuccess(firstResponse)) {
				throw new IllegalStateException();
			}

			int totalCount = storeOpenApiParser.extractTotalCount(firstResponse);
			List<StoreOpenApiResponse.Row> firstPageRows = storeOpenApiParser.extractRows(firstResponse);
			storeDataService.processAndSaveStores(firstPageRows);

			int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
			for (int page = 2; page <= totalPages; page++) {
				Thread.sleep(300);
				String json = storeOpenApiClient.fetchStoreData(page, PAGE_SIZE);
				StoreOpenApiResponse response = storeOpenApiParser.parse(json);
				if (!storeOpenApiParser.isSuccess(response)) continue;
				storeDataService.processAndSaveStores(storeOpenApiParser.extractRows(response));
			}

		} catch (Exception e) {
			log.error("Store sync error", e);
			throw new OpenApiException();
		}
	}
}
