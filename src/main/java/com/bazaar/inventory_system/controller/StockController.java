package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.exception.InsufficientStockException;
import com.bazaar.inventory_system.exception.InvalidStockRequestException;
import com.bazaar.inventory_system.exception.ProductNotFoundException;
import com.bazaar.inventory_system.model.*;
import com.bazaar.inventory_system.repository.ManagerRepository;
import com.bazaar.inventory_system.repository.StockMovementRepository;
import com.bazaar.inventory_system.repository.StockRepository;
import com.bazaar.inventory_system.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Cacheable(value = "stockByStore", key = "#storeId")
    @GetMapping("/stock")
    public ResponseEntity<List<Stock>>  getAllStockForStore(@PathVariable Long storeId) {
        List<Stock> stock = stockRepository.findByStoreId(storeId);
        return ResponseEntity.ok(stock);
    }
    // Get all stock for all stores with optional filters for productId, category, and name
    @GetMapping("/filter")
    public ResponseEntity<List<Stock>> filterAllStock(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name) {

        List<Stock> stocks;

        if (productId != null && category != null && name != null) {
            stocks = stockRepository.findByProductIdAndCategoryAndName(productId, category, name);
        } else if (productId != null && category != null) {
            stocks = stockRepository.findByProductIdAndCategory(productId, category);
        } else if (productId != null && name != null) {
            stocks = stockRepository.findByProductIdAndName(productId, name);
        } else if (category != null && name != null) {
            stocks = stockRepository.findByCategoryAndName(category, name);
        } else if (productId != null) {
            stocks = stockRepository.findByProductId(productId);
        } else if (category != null) {
            stocks = stockRepository.findByCategory(category);
        } else if (name != null) {
            stocks = stockRepository.findByName(name);
        } else {
            stocks = stockRepository.findAll(); // Default: get all stock
        }

        return ResponseEntity.ok(stocks);
    }
    // New method to filter stock by name or category within a specific store
    @GetMapping("/filter/{storeId}")
    public ResponseEntity<List<Stock>> filterStockByStore(
            @PathVariable Long storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {

        List<Stock> stocks;

        // Call the repository method to filter stock by storeId, name or category
        if (name != null && category != null) {
            stocks = stockRepository.findByStoreIdAndNameOrCategory(storeId, name, category);
        } else if (name != null) {
            stocks = stockRepository.findByStoreIdAndNameOrCategory(storeId, name, null);
        } else if (category != null) {
            stocks = stockRepository.findByStoreIdAndNameOrCategory(storeId, null, category);
        } else {
            // Return all stock in the store if no filters provided
            stocks = stockRepository.findByStoreId(storeId);
        }

        return ResponseEntity.ok(stocks);
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
            @RequestParam Long managerId,
            @RequestParam Long vendorId,
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

        // Record movement with manager and vendor
        createStockMovement(storeId, stock.getProductId(), stock.getQuantity(), "Stock-In", managerId, vendorId);

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
    private void createStockMovement(Long storeId, Long productId, int quantityChange, String movementType, Long managerId, Long vendorId) {
        // Fetch manager and vendor from repositories
        Manager manager = managerRepository.findById(managerId).orElseThrow(() -> new IllegalArgumentException("Manager not found"));
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        // Create and save StockMovement
        StockMovement movement = new StockMovement();
        movement.setStoreId(storeId);
        movement.setProductId(productId);
        movement.setQuantityChange(quantityChange);
        movement.setMovementType(movementType);
        movement.setTimestamp(LocalDateTime.now());
        movement.setManager(manager);
        movement.setVendor(vendor);

        stockMovementRepository.save(movement);
    }

}