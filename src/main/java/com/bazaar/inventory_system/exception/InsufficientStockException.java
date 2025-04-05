package com.bazaar.inventory_system.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, int requested, int available) {
        super("Insufficient stock for product " + productId +
                " (requested: " + requested + ", available: " + available + ")");
    }
}
