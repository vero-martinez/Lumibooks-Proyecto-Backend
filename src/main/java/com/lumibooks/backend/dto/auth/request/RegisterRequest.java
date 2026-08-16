package com.lumibooks.backend.dto.auth.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para recibir los datos de registro de un usuario.
 *
 * Valida que la información enviada cumpla con los requisitos
 * antes de crear una nueva cuenta.
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    // Nombre del usuario.
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String firstName;

    // Apellido del usuario.
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 150, message = "El apellido no puede superar los 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El apellido solo puede contener letras y espacios")
    private String lastName;

    // Correo electrónico del usuario.
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    //Contraseña del usuario.
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    // Documento Nacional de Identidad (DNI).
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;

    //Número de teléfono del usuario.
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
    private String cellphone;

    // Indica si el usuario aceptó los términos y condiciones.
    @AssertTrue(message = "Debes aceptar los términos y condiciones")
    private Boolean acceptsTerms;

    //Indica si el usuario desea recibir el boletín de novedades.
    private Boolean subscribedToNewsletter;

}