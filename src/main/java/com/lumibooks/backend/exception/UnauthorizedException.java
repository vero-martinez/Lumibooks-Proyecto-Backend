package com.lumibooks.backend.exception;

// Excepción personalizada para errores de autorización
public class UnauthorizedException extends RuntimeException {
    
    // Constructor con mensaje personalizado
    public UnauthorizedException(String message) {
        super(message);
    }
    
    // Constructor con mensaje personalizado y causa
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
