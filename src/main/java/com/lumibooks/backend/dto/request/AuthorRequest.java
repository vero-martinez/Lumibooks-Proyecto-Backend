package com.lumibooks.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Clase DTO (Data Transfer Object) para recibir los datos de un autor en las solicitudes de creación o actualización.
 * Funciones principales:
 * - Valida los campos de nombre y apellido usando anotaciones de Jakarta Validation.
 * - Campos obligatorios: firstName, lastName.
 * - Campos opcionales: biography, profileImageUrl.
 */
public class AuthorRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 150, message = "El apellido no puede superar los 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El apellido solo puede contener letras y espacios")
    private String lastName;

    private String biography;

    private String profileImageUrl;

}