package org.netway.dongnehankki.store.application;

public interface StoreChunCheonDataService {
	void saveStores(int pageIndex, int pageSize) throws Exception;
	void saveAllStores(int pageSize) throws Exception;
}
