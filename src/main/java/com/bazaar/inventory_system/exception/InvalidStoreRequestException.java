package com.bazaar.inventory_system.exception;


public class InvalidStoreRequestException extends RuntimeException {
    public InvalidStoreRequestException(String message) {
        super(message);
    }
}
