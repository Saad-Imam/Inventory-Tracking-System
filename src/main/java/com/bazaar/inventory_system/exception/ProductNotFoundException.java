package com.bazaar.inventory_system.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId, Long storeId) {
        super("Product with ID " + productId + " not found in store " + storeId);
    }
    public ProductNotFoundException(Long productId) {
        super("Product not found with ID: " + productId);
    }
    public ProductNotFoundException(String productName) {
        super("Product not found with name: " + productName);
    }
}