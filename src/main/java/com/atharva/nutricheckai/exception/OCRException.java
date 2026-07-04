package com.atharva.nutricheckai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OCRException extends RuntimeException {
    
    public OCRException(String message) {
        super(message);
    }
    
    public OCRException(String message, Throwable cause) {
        super(message, cause);
    }
}
