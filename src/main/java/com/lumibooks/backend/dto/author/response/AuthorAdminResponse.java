package com.lumibooks.backend.dto.author.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase DTO (Data Transfer Object) para enviar la información de un autor en administración.
 * Funciones principales:
 * - Proporciona una vista completa de los datos de un autor para uso en administración.
 * - Incluye información detallada como ID, nombre, apellido, biografía y URL de la imagen de perfil.
 * - También incluye campos de estado y fechas de creación y actualización para facilitar la gestión administrativa.
 */
public class AuthorAdminResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String biography;
    private String profileImageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}