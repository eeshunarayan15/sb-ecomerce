package com.ecommerce.sbecom.exception;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {

    private  String message;
    @Serial
    private  final Long  serialVersionUID = 1L;
    public ResourceNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}