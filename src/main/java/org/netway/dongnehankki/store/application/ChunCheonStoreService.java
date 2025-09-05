package org.netway.dongnehankki.store.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.external.AddressApiClient;
import org.netway.dongnehankki.store.infrastructure.external.ChunCheonOpenApiClient;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
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

	public void fetchAndSaveStores(int pageIndex, int pageSize) throws Exception {
		String response = openApiClient.fetchStoreData(pageIndex, pageSize);

		JsonNode root = objectMapper.readTree(response);
		JsonNode dataNode = root.get("data");

		List<Store> stores = new ArrayList<>();

		if (dataNode.isArray()) {
			for (JsonNode node : dataNode) {
				String sigun = node.get("시군").asText();
				if(!"춘천시".equals(sigun)) continue;

				String name = node.get("업체명").asText();
				String address = node.get("소재지주소").asText();

				double[] coords = addressApiClient.getCoordinates(address);
				Double latitude = coords[0];
				Double longitude = coords[1];

				//TODO 업체분류를 industryCode로 변경하는 코드 추가
				Store store = Store.createStore(
					name,
					latitude,   // latitude (위도)
					longitude,   // longitude (경도)
					address,
					sigun,
					2301,   // industryCode
					null    // businessRegistrationNumber
				);
				stores.add(store);

			}
		}

		storeRepository.saveAll(stores);
	}
}
