package com.bazaar.inventory_system.repository;

import com.bazaar.inventory_system.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
}