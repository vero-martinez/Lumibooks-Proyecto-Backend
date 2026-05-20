package com.lumibooks.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Clase DTO (Data Transfer Object) para recibir los datos de suscripción a la newsletter.
 * Funciones principales:
 * - Valida el campo de email que el usuario envía al suscribirse usando anotaciones de Jakarta Validation.
 * - Campo obligatorio: email.
 */
public class SubscribeRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

}
