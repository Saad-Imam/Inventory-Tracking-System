package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.exception.InvalidStockMovementException;
import com.bazaar.inventory_system.exception.StockMovementNotFoundException;
import com.bazaar.inventory_system.model.StockMovement;
import com.bazaar.inventory_system.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stores/{storeId}/stock-movements")
public class StockMovementController {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    // GET all stock movements with optional filters
    @Cacheable(value = "stockMovements", key = "#storeId")
    @GetMapping
    public ResponseEntity<List<StockMovement>> getStockMovements(
            @PathVariable Long storeId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // Validate date range if provided
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidStockMovementException("Start date must be before end date");
        }

        List<StockMovement> movements;
        if (productId != null && startDate != null && endDate != null) {
            movements = stockMovementRepository.findByStoreIdAndProductIdAndTimestampBetween(
                    storeId, productId, startDate, endDate);
        } else if (productId != null) {
            movements = stockMovementRepository.findByStoreIdAndProductId(storeId, productId);
        } else if (startDate != null && endDate != null) {
            movements = stockMovementRepository.findByStoreIdAndTimestampBetween(storeId, startDate, endDate);
        } else {
            movements = stockMovementRepository.findByStoreId(storeId);
        }

        return ResponseEntity.ok(movements);
    }
    // GET specific stock movement
    @Cacheable(value = "stockMovementById", key = "#storeId + '-' + #movementId")
    @GetMapping("/{movementId}")
    public ResponseEntity<StockMovement> getStockMovement(
            @PathVariable Long storeId,
            @PathVariable Long movementId) {

        StockMovement movement = stockMovementRepository.findById(movementId)
                .orElseThrow(() -> new StockMovementNotFoundException(movementId));

        // Verify movement belongs to the store
        if (!movement.getStoreId().equals(storeId)) {
            throw new InvalidStockMovementException(
                    "Stock movement " + movementId + " does not belong to store " + storeId);
        }

        return ResponseEntity.ok(movement);
    }

    // CREATE stock movement
    @CacheEvict(value = "stockMovements", key = "#stockMovement.storeId")
    @PostMapping
    public ResponseEntity<StockMovement> createStockMovement(
            @PathVariable Long storeId,
            @Valid @RequestBody StockMovement stockMovement) {

        // Validate store ID consistency
        if (!storeId.equals(stockMovement.getStoreId())) {
            throw new InvalidStockMovementException(
                    "Store ID in path and movement body must match");
        }

        // Validate quantity
        if (stockMovement.getQuantityChange() == 0) {
            throw new InvalidStockMovementException("Quantity change cannot be zero");
        }

        // Auto-set timestamp if not provided
        if (stockMovement.getTimestamp() == null) {
            stockMovement.setTimestamp(LocalDateTime.now());
        }

        StockMovement savedMovement = stockMovementRepository.save(stockMovement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovement);
    }

    // DELETE stock movement
    @Caching(evict = {@CacheEvict(value = "stockMovements", key = "#storeId"),
                      @CacheEvict(value = "stockMovementById", key = "#movementId")})
    @DeleteMapping("/{movementId}")
    public ResponseEntity<Void> deleteStockMovement(
            @PathVariable Long storeId,
            @PathVariable Long movementId) {

        StockMovement movement = stockMovementRepository.findById(movementId)
                .orElseThrow(() -> new StockMovementNotFoundException(movementId));

        // Verify store ownership
        if (!movement.getStoreId().equals(storeId)) {
            throw new InvalidStockMovementException(
                    "Cannot delete movement - it belongs to a different store");
        }

        stockMovementRepository.delete(movement);
        return ResponseEntity.noContent().build();
    }
}
