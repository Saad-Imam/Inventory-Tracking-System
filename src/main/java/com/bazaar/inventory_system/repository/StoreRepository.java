package com.bazaar.inventory_system.repository;

import com.bazaar.inventory_system.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}

