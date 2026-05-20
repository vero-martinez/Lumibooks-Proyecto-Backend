package com.lumibooks.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.request.SubscribeRequest;
import com.lumibooks.backend.dto.response.ApiResponse;
import com.lumibooks.backend.service.SubscriberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de suscripciones al newsletter desde el
 * landing page.
 * Permite a los usuarios suscribirse proporcionando su email.
 * No requiere autenticación, ya que está destinado a usuarios que aún no tienen
 * cuenta en el sistema.
 * Proporciona una respuesta con un mensaje de éxito al completar la
 * suscripción.
 */
@RestController
@RequestMapping("/api/public/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberService subscriberService;

    /**
     * Permite a un usuario suscribirse al newsletter proporcionando su email.
     * @param subscribeRequest Contiene el email del usuario que desea suscribirse a la newsletter.
     * @return Respuesta con un mensaje de éxito indicando que la suscripción se ha realizado correctamente.
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse> subscribe(@Valid @RequestBody SubscribeRequest subscribeRequest) {
        subscriberService.subscribeFromLanding(subscribeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Te has suscrito exitosamente")
                        .build());
    }

}