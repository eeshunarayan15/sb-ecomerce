package com.ecommerce.sbecom.exception;

import java.io.Serial;

public class InvalidCredentialsException extends RuntimeException{
    private  String message;
    @Serial
    private  final Long  serialVersionUID = 1L;
    public InvalidCredentialsException(String message) {
        super(message);
        this.message = message;
    }
}
