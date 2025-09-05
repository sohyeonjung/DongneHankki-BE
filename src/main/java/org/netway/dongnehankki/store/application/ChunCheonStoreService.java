package org.netway.dongnehankki.store.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.external.AddressApiClient;
import org.netway.dongnehankki.store.infrastructure.external.ChunCheonOpenApiClient;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChunCheonStoreService {

	private final ChunCheonOpenApiClient openApiClient;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final StoreRepository storeRepository;
	private final AddressApiClient addressApiClient;

	private static final Set<String> VALID_INDU_TYPE_CODES = Set.of(
		"2502", "2301", "2105", "5201"
	);

	private static final Map<String, Integer> INDUSTRY_CODE_MAP = Map.of(
		"음ㆍ식료품 위주 종합 소매업", 5201,
		"식료품 소매업", 5201,
		"기타 간이 음식점업", 2502,
		"한식 음식점업", 2502
	);
	private final SwaggerIndexTransformer indexPageTransformer;

	public void fetchAndSaveStores(int pageIndex, int pageSize) throws Exception {
		String response = openApiClient.fetchStoreData(pageIndex, pageSize);

		JsonNode root = objectMapper.readTree(response);
		JsonNode dataNode = root.get("data");

		List<Store> stores = new ArrayList<>();

		if (dataNode.isArray()) {
			for (JsonNode node : dataNode) {
				String sigun = node.get("시군").asText();
				if(!"춘천시".equals(sigun)) continue;

				String type = node.get("업종").asText();
				Integer industryCode = INDUSTRY_CODE_MAP.get(type);
				if (industryCode == null) continue;

				String name = node.get("업체명").asText();
				String address = node.get("소재지주소").asText();

				double[] coords = addressApiClient.getCoordinates(address);
				Double latitude = coords[0];
				Double longitude = coords[1];
				if(latitude==0||longitude==0) continue;

				Store store = Store.createStore(
					name,
					latitude,
					longitude,
					address,
					sigun,
					industryCode,
					null    // businessRegistrationNumber
				);
				stores.add(store);

			}
		}

		storeRepository.saveAll(stores);
	}

	public void fetchAndSaveAllStores(int pageSize) throws Exception {
		String firstPageResponse = openApiClient.fetchStoreData(1, pageSize);
		JsonNode root = objectMapper.readTree(firstPageResponse);
		int totalCount = root.get("totalCount").asInt();
		int totalPages = (int) Math.ceil((double) totalCount / pageSize);

		for (int page = 1; page <= totalPages; page++) {
			Thread.sleep(300); // API 호출 딜레이
			fetchAndSaveStores(page, pageSize);
		}
	}
}
