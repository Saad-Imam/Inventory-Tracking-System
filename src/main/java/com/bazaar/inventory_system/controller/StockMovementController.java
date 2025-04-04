package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.model.StockMovement;
import com.bazaar.inventory_system.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stores/{storeId}")
public class StockMovementController {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @GetMapping("/stock-movements")
    public List<StockMovement> getStockMovements(
            /*The RequestParam extracts from query parameters, while pathVariable extracts from URL itself*/
            @PathVariable Long storeId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        if (productId != null && startDate != null && endDate != null) {
            return stockMovementRepository.findByStoreIdAndProductIdAndTimestampBetween(
                    storeId, productId, startDate, endDate);
        } else {
            return stockMovementRepository.findAll(); // Or handle other filtering combinations
        }
    }

    @PostMapping("/stock-movements")
    public StockMovement addStockMovement(
            @PathVariable Long storeId,
            @RequestBody StockMovement stockMovement) {
        /*This annotation indicates that the method expects the body of the HTTP POST request to contain a JSON
        representation of a StockMovement object.*/
        stockMovement.setStoreId(storeId);
        return stockMovementRepository.save(stockMovement);
    }
}
