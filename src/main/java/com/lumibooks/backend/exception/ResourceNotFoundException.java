package com.lumibooks.backend.exception;

// Excepción para indicar que un recurso no fue encontrado
public class ResourceNotFoundException extends RuntimeException{
    
    // Constructor con mensaje personalizado
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    // Constructor con mensaje personalizado y causa
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
