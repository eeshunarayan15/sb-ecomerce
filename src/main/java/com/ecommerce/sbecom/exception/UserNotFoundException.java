package com.ecommerce.sbecom.exception;

import java.io.Serial;

public class UserNotFoundException extends  RuntimeException{

    private  String message;
    @Serial
    private  final  long serialVersionUID = 1L;
    public UserNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
