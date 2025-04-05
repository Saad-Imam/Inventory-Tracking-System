package com.bazaar.inventory_system.exception;

public class StockMovementNotFoundException extends RuntimeException {
    public StockMovementNotFoundException(Long movementId) {
        super("Stock movement not found with ID: " + movementId);
    }
}
