package org.netway.dongnehankki.store.infrastructure.repository;

import org.netway.dongnehankki.store.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
