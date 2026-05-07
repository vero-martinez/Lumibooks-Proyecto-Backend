package com.lumibooks.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String token; // Token JWT generado para el usuario autenticado
    private String email; // Email del usuario autenticado
    private String nombre; // Nombre del usuario autenticado
    private String apellido; // Apellido del usuario autenticado
    private String rol; // Rol del usuario autenticado
    private String mensaje; // Mensaje adicional, como "Inicio de sesión exitoso"
}
