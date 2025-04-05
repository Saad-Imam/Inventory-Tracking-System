package com.bazaar.inventory_system.exception;

public class InvalidStockMovementException extends RuntimeException {
    public InvalidStockMovementException(String message) {
        super(message);
    }
}
