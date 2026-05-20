package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.request.SubscribeRequest;
import com.lumibooks.backend.dto.response.SubscriberDetailResponse;
import com.lumibooks.backend.dto.response.SubscriberSummaryResponse;
import com.lumibooks.backend.entity.User;

/**
 * Interfaz para la gestión de suscriptores.
 * Define las operaciones disponibles para suscribir usuarios a la newsletter, 
 * obtener listados de suscriptores con filtros y paginación,
 * obtener detalles de un suscriptor por ID y activar/desactivar suscripciones.
 */
public interface SubscriberService {
    
    /**
     * Suscribe a un usuario a la newsletter al momento de registrarse.
     * @param user El usuario que se acaba de registrar y que se desea suscribir a la newsletter.
     */
    void subscribeFromRegister(User user);
    
    /**
     * Suscribe a un usuario a la newsletter desde la landing page.
     * @param subscriberRequest Contiene el email del usuario que desea suscribirse a la newsletter.
     * @return SubscriberResponse con los datos de la suscripción creada.
     */
    void subscribeFromLanding(SubscribeRequest subscribeRequest);

    /**
     * Obtiene una lista paginada de suscriptores aplicando filtros opcionales.
     * @param email filtro por email (opcional)
     * @param isActive filtro por estado de suscripción (opcional)
     * @param pageable parámetros de paginación y ordenamiento
     * @return página de suscriptores que cumplen con los filtros aplicados
     */
    Page<SubscriberSummaryResponse> getSubscribers(String email, Boolean isActive, Pageable pageable);

    /**
     * Obtiene los detalles de un suscriptor por su ID.
     * @param id identificador del suscriptor
     * @return detalles del suscriptor encontrado
     */
    SubscriberDetailResponse getSubscriberById(Long id);

    /**
     * Alterna el estado de suscripción de un suscriptor.
     * @param id identificador del suscriptor
     */
    void toggleSubscriberStatus(Long id);

}
