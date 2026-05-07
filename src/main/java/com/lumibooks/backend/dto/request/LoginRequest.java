package com.lumibooks.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/**
 * Clase DTO (Data Transfer Object) para recibir los datos de inicio de sesión de un
 * usuario.
 * 
 * Funciones principales:
 * - Valida los campos que el usuario envía al iniciar sesión usando anotaciones de
 * Jakarta Validation.
 * - Campos obligatorios: email y contraseña.
 * 
 * Esta clase se utiliza en el endpoint de inicio de sesión para asegurar que los datos
 * recibidos cumplen con las reglas antes de procesarlos en el backend.
 */
public class LoginRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 carácteres")
    private String password;
    
}
