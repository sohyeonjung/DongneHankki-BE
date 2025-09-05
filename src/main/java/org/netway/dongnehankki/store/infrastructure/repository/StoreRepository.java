package org.netway.dongnehankki.store.infrastructure.repository;

import org.netway.dongnehankki.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreId(Long storeId);

	Optional<Store> findByBusinessRegistrationNumber(Long businessRegistrationNumber);

	List<Store> findByLatitudeBetweenAndLongitudeBetween(
		double minLatitude, double maxLatitude, double minLongitude, double maxLongitude
	);
	List<Store> findByLatitudeBetweenAndLongitudeBetweenAndIndustryCode(
		double minLat, double maxLat, double minLon, double maxLon,
		Integer industryCode
	);
	List<Store> findByLatitudeBetweenAndLongitudeBetweenAndNameContaining(
		double minLat, double maxLat, double minLon, double maxLon, String name);
	List<Store> findByLatitudeBetweenAndLongitudeBetweenAndIndustryCodeAndNameContaining(
		double minLat, double maxLat, double minLon, double maxLon,
		Integer industryCode,
		String name
	);

	List<Store> findByNameContaining(String name);
}
