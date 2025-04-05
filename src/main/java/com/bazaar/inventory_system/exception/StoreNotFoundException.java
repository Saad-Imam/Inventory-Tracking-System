package com.bazaar.inventory_system.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(Long storeId) {
        super("Store not found with ID: " + storeId);
    }
}

