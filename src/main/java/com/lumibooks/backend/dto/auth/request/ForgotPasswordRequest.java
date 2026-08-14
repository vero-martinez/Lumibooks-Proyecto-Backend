package com.lumibooks.backend.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para solicitar el envío de un código de recuperación de
 * contraseña.
 */
@Getter
@Setter
@AllArgsConstructor
public class ForgotPasswordRequest {

    // Correo electrónico de la cuenta a recuperar.
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;
    
}