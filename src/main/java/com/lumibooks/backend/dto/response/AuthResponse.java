package com.lumibooks.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/**
 * Clase DTO (Data Transfer Object) para enviar los datos de respuesta de autenticación.
 * 
 * Funciones principales:
 * - Contiene la información necesaria para responder a una solicitud de autenticación exitosa.
 * - Incluye el token JWT, los datos del usuario y un mensaje de confirmación.
 */
public class AuthResponse {

    private String token; // Token JWT generado para el usuario autenticado
    private String tokenType = "Bearer"; // Tipo de token, generalmente "Bearer"
    private String email; // Email del usuario autenticado
    private String nombre; // Nombre del usuario autenticado
    private String apellido; // Apellido del usuario autenticado
    private String rol; // Rol del usuario autenticado
    private String mensaje; // Mensaje adicional, como "Inicio de sesión exitoso"
}
