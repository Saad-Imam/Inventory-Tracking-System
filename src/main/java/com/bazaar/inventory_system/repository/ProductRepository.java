package com.bazaar.inventory_system.repository;
import com.bazaar.inventory_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // For name-only search
    List<Product> findByNameContainingIgnoreCase(String name);

    // For category-only search
    List<Product> findByCategory(String category);

    // For combined search
    List<Product> findByNameContainingIgnoreCaseAndCategory(String name, String category);
}
