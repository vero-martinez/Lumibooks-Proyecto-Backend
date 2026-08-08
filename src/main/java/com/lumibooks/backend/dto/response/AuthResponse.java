package com.lumibooks.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para enviar la respuesta de una autenticación exitosa.
 *
 * Contiene el Access Token y la información básica del usuario
 * autenticado.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {

    // Access Token JWT generado para el usuario autenticado.
    private String token;

    // Tipo de token utilizado para la autenticación.
    @Builder.Default
    private String tokenType = "Bearer";

    // Correo electrónico del usuario.
    private String email;

    // Nombre del usuario.
    private String firstName;

    // Apellido del usuario.
    private String lastName;

    // Rol asignado al usuario.
    private String role;

    // Mensaje descriptivo del resultado de la autenticación.
    private String message;
}