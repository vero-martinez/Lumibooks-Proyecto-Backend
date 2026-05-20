package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.response.SubscriberDetailResponse;
import com.lumibooks.backend.dto.response.SubscriberSummaryResponse;
import com.lumibooks.backend.service.SubscriberService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de suscriptores.
 * Solo accesible por usuarios con rol ADMIN.
 * Permite listar suscriptores con filtros, obtener detalles de un suscriptor y activar/desactivar suscripciones.
 */
@RestController
@RequestMapping("/api/admin/subscribers")
@RequiredArgsConstructor
public class SubscriberAdminController {

    private final SubscriberService subscriberService;

    /**
     * Lista suscriptores aplicando filtros opcionales y paginación.
     * @param email filtro por email (opcional)
     * @param isActive filtro por estado de suscripción (opcional)
     * @param pageable parámetros de paginación y ordenamiento
     * @return página de suscriptores que cumplen con los filtros aplicados
     */
    @GetMapping
    public ResponseEntity<Page<SubscriberSummaryResponse>> getSubscribers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(subscriberService.getSubscribers(email, isActive, pageable));
    }

    /**
     * Obtiene los detalles de un suscriptor por su ID.
     * @param id identificador del suscriptor
     * @return detalles del suscriptor encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubscriberDetailResponse> getSubscriberById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriberService.getSubscriberById(id));
    }

    /**
     * Alterna el estado de suscripción de un suscriptor.
     * @param id identificador del suscriptor
     * @return respuesta sin contenido
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleSubscriberStatus(@PathVariable Long id) {
        subscriberService.toggleSubscriberStatus(id);
        return ResponseEntity.noContent().build();
    }

}