package com.lumibooks.backend.exception;

/**
 * Excepción personalizada para errores de solicitudes inválidas.
 *
 * Se utiliza cuando los datos enviados por el cliente no cumplen
 * con las reglas o validaciones esperadas por la aplicación.
 */
public class BadRequestException extends RuntimeException {

    // Crea una excepción con un mensaje descriptivo.
    public BadRequestException(String message) {
        super(message);
    }

    // Crea una excepción con mensaje y la causa original del error.
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}