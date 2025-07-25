package org.netway.dongnehankki.store.infrastructure.repository;

import org.netway.dongnehankki.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreId(Long storeId);

    List<Store> findByLatitudeBetweenAndLongitudeBetween(
        Double minLatitude, Double maxLatitude,
        Double minLongitude, Double maxLongitude
    );

	Optional<Store> findByBusinessRegistrationNumber(Long businessRegistrationNumber);
}
