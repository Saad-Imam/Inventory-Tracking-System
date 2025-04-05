package com.bazaar.inventory_system.exception;

public class InvalidStockRequestException extends RuntimeException {
    public InvalidStockRequestException(String message) {
        super(message);
    }
}
