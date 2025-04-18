package com.bazaar.inventory_system.repository;
import com.bazaar.inventory_system.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByStoreIdAndProductIdAndTimestampBetween(
            Long storeId, Long productId, LocalDateTime startDate, LocalDateTime endDate);
    List<StockMovement> findByStoreId(
            Long storeId);
    List<StockMovement> findByStoreIdAndTimestampBetween(
            Long storeId, LocalDateTime startDate, LocalDateTime endDate);
    List<StockMovement> findByStoreIdAndProductId(
            Long storeId, Long productId);

}

