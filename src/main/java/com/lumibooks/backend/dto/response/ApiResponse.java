package com.lumibooks.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase DTO (Data Transfer Object) para enviar respuestas genéricas de la API.
 * Funciones principales:
 * - Contiene un mensaje que describe el resultado de una operación (ej. éxito, error, etc.).
 */
public class ApiResponse {
    private String message;
}