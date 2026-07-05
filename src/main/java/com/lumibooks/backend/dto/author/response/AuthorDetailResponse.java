package com.lumibooks.backend.dto.author.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase DTO (Data Transfer Object) para enviar la información pública de un autor.
 * Funciones principales:
 * - Proporciona una vista pública de los datos de un autor.
 * - Incluye información básica como ID, nombre y apellido.
 */
public class AuthorDetailResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String biography;
    private String profileImageUrl;

}