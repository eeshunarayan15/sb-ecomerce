package com.ecommerce.sbecom.exception;

public class ProductAlreadyInCartException extends RuntimeException {
    public ProductAlreadyInCartException(String message) {
        super(message);
    }
}