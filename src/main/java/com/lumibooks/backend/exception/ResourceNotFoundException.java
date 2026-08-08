package com.lumibooks.backend.exception;

/**
 * Excepción personalizada para indicar que un recurso no existe.
 *
 * Se utiliza cuando la aplicación intenta obtener un dato que no
 * fue encontrado en la base de datos o en alguna fuente de información.
 */
public class ResourceNotFoundException extends RuntimeException {

    // Crea una excepción con un mensaje descriptivo.
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Crea una excepción con mensaje y la causa original del error.
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}