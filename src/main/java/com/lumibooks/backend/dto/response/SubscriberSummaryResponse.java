package com.lumibooks.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase DTO (Data Transfer Object) para enviar un resumen de la información de un suscriptor.
 * Funciones principales:
 * - Contiene información básica de un suscriptor, como su email, estado de la suscripción y fecha de creación.
 * - Se utiliza para mostrar listados de suscriptores en la interfaz de administración.
 */
public class SubscriberSummaryResponse {

    private Long id;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Boolean isUser; // indica si está linkeado a un usuario

}