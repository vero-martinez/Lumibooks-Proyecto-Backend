package com.lumibooks.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase DTO (Data Transfer Object) para enviar los detalles completos de un suscriptor.
 * Funciones principales:
 * - Contiene toda la información relevante de un suscriptor, incluyendo datos personales y estado de la suscripción.
 * - Se utiliza para mostrar los detalles de un suscriptor específico en la interfaz de administración.
 */
public class SubscriberDetailResponse {

    private Long id;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String firstName;
    private String lastName;
    private String userEmail;
    private String dni;
    private String cellphone;

}