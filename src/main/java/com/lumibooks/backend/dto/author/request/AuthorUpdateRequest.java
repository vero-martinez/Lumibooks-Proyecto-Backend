package com.lumibooks.backend.dto.author.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir los datos de un autor en las solicitudes de actualización parcial.
 * Todos los campos son opcionales. La imagen de perfil se recibe como RequestPart separado en el controller.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorUpdateRequest {

    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String firstName;

    @Size(max = 150, message = "El apellido no puede superar los 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El apellido solo puede contener letras y espacios")
    private String lastName;

    private String biography;

}