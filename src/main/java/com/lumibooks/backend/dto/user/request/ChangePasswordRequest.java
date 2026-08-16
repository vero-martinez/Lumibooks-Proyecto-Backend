package com.lumibooks.backend.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el cambio de contraseña del propio usuario autenticado.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    // Contraseña actual del usuario (se valida antes de cambiar).
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String currentPassword;

    // Nueva contraseña del usuario.
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String newPassword;

}