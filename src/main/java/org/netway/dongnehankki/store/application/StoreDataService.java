package org.netway.dongnehankki.store.application;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.dto.response.StoreOpenApiResponse;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreDataService {

	private final StoreRepository storeRepository;

	private static final Set<String> VALID_INDU_TYPE_CODES = Set.of(
		"2102", "2103", "2104", "2105",
		"2201",
		"2301", "2302", "2303", "2305", "2309", "2310",
		"2501", "2502",
		"2601",
		"5201", "5202"
	);


	@Transactional
	public void processAndSaveStores(List<StoreOpenApiResponse.Row> apiRows) {
		if (apiRows == null || apiRows.isEmpty()) {
			log.info("No store data to process.");
			return;
		}

		int newStoresCount = 0;
		int updatedStoresCount = 0;

		for (StoreOpenApiResponse.Row row : apiRows) {
			// null 값 확인
			if(row.getLeadTaxManStateCd()==null || row.getCmpnmNm()==null|| row.getRefineWgs84Lat()==null ||
				row.getRefineWgs84Logt()==null || row.getRefineRoadnmAddr()==null || row.getSigunNm()==null ||
				row.getIndutypeCd()==null || row.getBizregno()==null){
				log.debug("Skipping row: {}", row);
				continue;
			}

			// 휴폐업이 아닌 경우
			if (!row.getLeadTaxManStateCd().equals("01")) {
				continue;
			}

			// 음식점만
			String indutypeCd = row.getIndutypeCd();
			if (indutypeCd.isBlank() || !VALID_INDU_TYPE_CODES.contains(indutypeCd)) {
				continue;
			}

			// 사업자등록번호로 존재하는 가게 확인
			String businessRegistrationNumber = row.getBizregno();
			Optional<Store> existingStore = storeRepository.findByBusinessRegistrationNumber(businessRegistrationNumber);

			Store store;
			if (existingStore.isPresent()) {
				store = existingStore.get();
				store.updateStore(row.getCmpnmNm(), parseToDouble(row.getRefineWgs84Lat()), parseToDouble(row.getRefineWgs84Logt()),
					row.getRefineRoadnmAddr(), row.getSigunNm(), row.getIndutypeCd());
				updatedStoresCount++;
			} else {
				store = Store.createStore(row.getCmpnmNm(), parseToDouble(row.getRefineWgs84Lat()), parseToDouble(row.getRefineWgs84Logt()),
					row.getRefineRoadnmAddr(), row.getSigunNm(), row.getIndutypeCd(), row.getBizregno());
				newStoresCount++;
			}
			storeRepository.save(store);
		}
		log.info("Store data processing complete. New stores: {}, Updated stores: {}", newStoresCount, updatedStoresCount);
	}

	private Double parseToDouble(String value) {
		try {
			return (value != null && !value.trim().isEmpty()) ? Double.parseDouble(value) : null;
		} catch (NumberFormatException e) {
			log.warn("Failed to parse double value: {}. Returning null.", value, e);
			return null;
		}
	}
}
