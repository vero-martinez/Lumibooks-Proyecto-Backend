package com.lumibooks.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase DTO (Data Transfer Object) para enviar un resumen de la información de un autor en administración.
 * Funciones principales:
 * - Proporciona una vista resumida de los datos de un autor.
 * - Incluye información esencial como ID, nombre, apellido y URL de la imagen de perfil.
 */
public class AuthorSummaryResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;

}