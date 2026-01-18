package com.ecommerce.sbecom.exception;

import java.io.Serial;

public class UserAlreadyExistsException  extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    String message;
    public UserAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }
}
