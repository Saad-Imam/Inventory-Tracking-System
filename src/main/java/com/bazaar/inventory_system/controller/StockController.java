package com.bazaar.inventory_system.controller;

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
    public List<Stock> getAllStockForStore(@PathVariable Long storeId) {
        // This might need a custom query in StockRepository for more complex filtering
        return stockRepository.findAll(); // Simplest version for now
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
        StockId stockId = new StockId();
        stockId.setStoreId(storeId);
        stockId.setProductId(stock.getProductId());
        stock.setStoreId(storeId);

        Optional<Stock> existingStock = stockRepository.findById(stockId);
        if (existingStock.isPresent()) {
            Stock stockToUpdate = existingStock.get();
            stockToUpdate.setQuantity(stockToUpdate.getQuantity() + stock.getQuantity());
            stockRepository.save(stockToUpdate);
        } else {
            stockRepository.save(stock);
        }

        // Create a StockMovement record
        StockMovement stockMovement = new StockMovement();
        stockMovement.setStoreId(storeId);
        stockMovement.setProductId(stock.getProductId());
        stockMovement.setQuantityChange(stock.getQuantity());
        stockMovement.setMovementType("Stock-In");
        stockMovement.setTimestamp(LocalDateTime.now());
        stockMovementRepository.save(stockMovement);

        return ResponseEntity.status(HttpStatus.CREATED).body(stock);
    }

    @PostMapping("/sell")
    public ResponseEntity<Stock> sellProduct(
            @PathVariable Long storeId,
            @RequestBody Stock stock) {
        StockId stockId = new StockId();
        stockId.setStoreId(storeId);
        stockId.setProductId(stock.getProductId());

        Optional<Stock> existingStock = stockRepository.findById(stockId);
        if (existingStock.isPresent()) {
            Stock stockToUpdate = existingStock.get();
            if (stockToUpdate.getQuantity() >= stock.getQuantity()) {
                stockToUpdate.setQuantity(stockToUpdate.getQuantity() - stock.getQuantity());
                stockRepository.save(stockToUpdate);

                // Create a StockMovement record
                StockMovement stockMovement = new StockMovement();
                stockMovement.setStoreId(storeId);
                stockMovement.setProductId(stock.getProductId());
                stockMovement.setQuantityChange(-stock.getQuantity());
                stockMovement.setMovementType("Sale");
                stockMovement.setTimestamp(LocalDateTime.now());
                stockMovementRepository.save(stockMovement);
                return ResponseEntity.ok(stockToUpdate);
            } else {
                return ResponseEntity.badRequest().body(null); // Or throw an exception: "Insufficient stock"
            }
        } else {
            return ResponseEntity.notFound().build(); // Or throw an exception: "Product not found in stock"
        }
    }

    @PostMapping("/remove-stock")
    public ResponseEntity<Stock> removeStock(
            @PathVariable Long storeId,
            @RequestBody Stock stock) {
        StockId stockId = new StockId();
        stockId.setStoreId(storeId);
        stockId.setProductId(stock.getProductId());

        Optional<Stock> existingStock = stockRepository.findById(stockId);
        if (existingStock.isPresent()) {
            Stock stockToUpdate = existingStock.get();
            if (stockToUpdate.getQuantity() >= stock.getQuantity()) {
                stockToUpdate.setQuantity(stockToUpdate.getQuantity() - stock.getQuantity());
                stockRepository.save(stockToUpdate);

                // Create a StockMovement record
                StockMovement stockMovement = new StockMovement();
                stockMovement.setStoreId(storeId);
                stockMovement.setProductId(stock.getProductId());
                stockMovement.setQuantityChange(-stock.getQuantity());
                stockMovement.setMovementType("Removal");
                stockMovement.setTimestamp(LocalDateTime.now());
                stockMovementRepository.save(stockMovement);
                return ResponseEntity.ok(stockToUpdate);
            } else {
                return ResponseEntity.badRequest().body(null); // Or throw an exception: "Insufficient stock"
            }
        } else {
            return ResponseEntity.notFound().build(); // Or throw an exception: "Product not found in stock"
        }
    }
}