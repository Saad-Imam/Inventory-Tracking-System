package com.bazaar.inventory_system.repository;
import com.bazaar.inventory_system.model.Stock;
import com.bazaar.inventory_system.model.StockId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, StockId> {
}
