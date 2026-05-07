package com.lumibooks.backend.exception;

// Excepción para indicar que la solicitud es incorrecta o no cumple con los requisitos
public class BadRequestException extends RuntimeException {
    
    // Constructor con mensaje personalizado
    public BadRequestException(String message) {
        super(message);
    }
    
    // Constructor con mensaje personalizado y causa
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
