package com.lumibooks.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO utilizado para enviar respuestas simples de la API.
 *
 * Contiene un mensaje que describe el resultado de una operación,
 * como una confirmación de éxito o un mensaje de error.
 */
@Getter
@Builder
public class ApiResponse {

    // Mensaje descriptivo de la respuesta.
    private String message;

}