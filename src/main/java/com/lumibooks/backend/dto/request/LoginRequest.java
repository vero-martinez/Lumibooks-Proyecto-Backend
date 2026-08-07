package com.lumibooks.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para recibir las credenciales de inicio de sesión.
 *
 * Valida que el email tenga un formato correcto y que la
 * contraseña cumpla con los requisitos mínimos antes de
 * procesar la autenticación.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    // Correo electrónico del usuario.
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    // Contraseña del usuario.
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 carácteres")
    private String password;

}