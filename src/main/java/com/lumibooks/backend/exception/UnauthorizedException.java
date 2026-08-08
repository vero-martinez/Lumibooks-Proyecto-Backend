package com.lumibooks.backend.exception;

/**
 * Excepción personalizada para errores de autorización.
 *
 * Se utiliza cuando un usuario no tiene permisos o credenciales
 * válidas para realizar una operación.
 */
public class UnauthorizedException extends RuntimeException {

    // Crea una excepción con un mensaje descriptivo.
    public UnauthorizedException(String message) {
        super(message);
    }

    // Crea una excepción con mensaje y la causa original del error.
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

}