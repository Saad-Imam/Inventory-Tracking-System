package com.bazaar.inventory_system.repository;

import com.bazaar.inventory_system.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}