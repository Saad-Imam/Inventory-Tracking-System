package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.exception.InsufficientStockException;
import com.bazaar.inventory_system.exception.InvalidStockRequestException;
import com.bazaar.inventory_system.exception.ProductNotFoundException;
import com.bazaar.inventory_system.model.Stock;
import com.bazaar.inventory_system.model.StockId;
import com.bazaar.inventory_system.model.StockMovement;
import com.bazaar.inventory_system.repository.StockMovementRepository;
import com.bazaar.inventory_system.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stores/{storeId}")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @GetMapping("/stock")
    public ResponseEntity<List<Stock>>  getAllStockForStore(@PathVariable Long storeId) {
        // This might need a custom query in StockRepository for more complex filtering
        List<Stock> stock = stockRepository.findByStoreId(storeId);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/stock/{productId}")
    public Optional<Stock> getStockForProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) {
        StockId stockId = new StockId();
        stockId.setStoreId(storeId);
        stockId.setProductId(productId);
        return stockRepository.findById(stockId);
    }

    @PostMapping("/stock-in")
    public ResponseEntity<Stock> addStock(
            @PathVariable Long storeId,
            @RequestBody Stock stock) {

        // Validate input
        if (stock.getQuantity() <= 0) {
            throw new InvalidStockRequestException("Quantity must be positive");
        }

        StockId stockId = new StockId(storeId, stock.getProductId());
        stock.setStoreId(storeId);

        Stock updatedStock = stockRepository.findById(stockId)
                .map(existingStock -> {
                    existingStock.setQuantity(existingStock.getQuantity() + stock.getQuantity());
                    return stockRepository.save(existingStock);
                })
                .orElseGet(() -> stockRepository.save(stock));

        // Record movement
        createStockMovement(storeId, stock.getProductId(), stock.getQuantity(), "Stock-In");
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedStock);
    }
    @PostMapping("/sell")
    public ResponseEntity<Stock> sellProduct(
            @PathVariable Long storeId,
            @RequestBody Stock stock) {

        // Validate input
        if (stock.getQuantity() <= 0) {
            throw new InvalidStockRequestException("Sale quantity must be positive");
        }

        StockId stockId = new StockId(storeId, stock.getProductId());
        Stock existingStock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ProductNotFoundException(stock.getProductId()));

        // Check sufficient stock
        if (existingStock.getQuantity() < stock.getQuantity()) {
            throw new InsufficientStockException(storeId, stock.getQuantity(), existingStock.getQuantity());
        }

        // Update stock
        existingStock.setQuantity(existingStock.getQuantity() - stock.getQuantity());
        stockRepository.save(existingStock);

        // Record movement with NEGATIVE quantity
        StockMovement movement = new StockMovement();
        movement.setStoreId(storeId);
        movement.setProductId(stock.getProductId());
        movement.setQuantityChange(-stock.getQuantity()); // Negative for sales
        movement.setMovementType("SALE");
        stockMovementRepository.save(movement);

        return ResponseEntity.ok(existingStock);
    }

    @PostMapping("/remove-stock")
    public ResponseEntity<Stock> removeStock(
            @PathVariable Long storeId,
            @RequestBody Stock stock) {

        // Validate input
        if (stock.getQuantity() <= 0) {
            throw new InvalidStockRequestException("Quantity must be positive");
        }

        StockId stockId = new StockId(storeId, stock.getProductId());

        // Find and validate stock
        Stock updatedStock = stockRepository.findById(stockId)
                .map(existingStock -> {
                    if (existingStock.getQuantity() < stock.getQuantity()) {
                        throw new InsufficientStockException(
                                stock.getProductId(),
                                stock.getQuantity(),
                                existingStock.getQuantity()
                        );
                    }
                    existingStock.setQuantity(existingStock.getQuantity() - stock.getQuantity());
                    return stockRepository.save(existingStock);
                })
                .orElseThrow(() -> new ProductNotFoundException(stock.getProductId(), storeId));

        // Record movement
        createStockMovement(storeId, stock.getProductId(), -stock.getQuantity(), "Removal");
        return ResponseEntity.ok(updatedStock);
    }
    // --- Helper Methods ---
    private void validateStockRequest(Stock stock) {
        if (stock.getQuantity() <= 0) {
            throw new InvalidStockRequestException("Quantity must be positive");
        }
    }

    private void createStockMovement(Long storeId, Long productId, int quantityChange, String movementType) {
        StockMovement movement = new StockMovement();
        movement.setStoreId(storeId);
        movement.setProductId(productId);
        movement.setQuantityChange(quantityChange);
        movement.setMovementType(movementType);
        movement.setTimestamp(LocalDateTime.now());
        stockMovementRepository.save(movement);
    }

}