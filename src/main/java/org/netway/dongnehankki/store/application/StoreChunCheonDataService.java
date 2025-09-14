package org.netway.dongnehankki.store.application;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface StoreChunCheonDataService {
	void saveStores(int pageIndex, int pageSize) throws Exception;
	void saveAllStores(int pageSize) throws Exception;
}
