package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.SubscribeRequest;
import com.lumibooks.backend.dto.response.SubscriberDetailResponse;
import com.lumibooks.backend.dto.response.SubscriberSummaryResponse;
import com.lumibooks.backend.entity.Subscriber;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.SubscriberRepository;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.SubscriberService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de suscriptores.
 * Se encarga de manejar la lógica de negocio relacionada con las suscripciones a la newsletter,
 * tanto para suscripciones desde el registro de usuarios como desde la landing page, así como
 * la gestión administrativa de los suscriptores (listado, detalles y activación/desactivación).
 */
@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final UserRepository userRepository;

    private final ActionLogService actionLogService;

    /**
     * Suscribe a un usuario a la newsletter al momento de registrarse.
     * Si el email del usuario ya existe como suscriptor sin usuario asociado, se vincula
     * Si no existe, se crea una nueva suscripción con el email del usuario.
     * Si ya existe una suscripción con usuario asociado, no se realiza ninguna acción.
     * @param user El usuario que se acaba de registrar y que se desea suscribir.
     */
    @Override
    @Transactional
    public void subscribeFromRegister(User user) {
        subscriberRepository.findByEmail(user.getEmail()).ifPresentOrElse(
                existing -> {
                    if (existing.getUser() == null) {
                        existing.setUser(user);
                        subscriberRepository.save(existing);
                    }
                },
                () -> {
                    Subscriber subscriber = Subscriber.builder()
                            .email(user.getEmail())
                            .user(user)
                            .isActive(true)
                            .build();
                    subscriberRepository.save(subscriber);
                });
    }

    /**
     * Suscribe a un usuario a la newsletter desde la landing page.
     * Verifica que el email no esté ya suscrito. Si el email ya existe
     * como suscriptor sin usuario asociado, lanza una excepción indicando que el email ya está suscrito.
     * Si el email no existe, crea una nueva suscripción con el email proporcionado.
      * @param subscribeRequest Contiene el email del usuario que desea suscribirse a la newsletter.
      * @throws BadRequestException si el email ya está suscrito sin usuario asociado.
     */
    @Override
    @Transactional
    public void subscribeFromLanding(SubscribeRequest subscribeRequest) {

        if (subscriberRepository.existsByEmail(subscribeRequest.getEmail())) {
            throw new BadRequestException("Este email ya está suscrito, utiliza otro email");
        }

        User user = userRepository.findByEmail(subscribeRequest.getEmail()).orElse(null);

        Subscriber subscriber = Subscriber.builder()
                .email(subscribeRequest.getEmail())
                .user(user)
                .isActive(true)
                .build();

        subscriberRepository.save(subscriber);
    }

    /**
     * Obtiene una lista paginada de suscriptores aplicando filtros opcionales.
     * @param email el email a filtrar
     * @param isActive el estado de la suscripción a filtrar
     * @param pageable la configuración de paginación
     * @return la página de suscriptores que cumplen con los filtros
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SubscriberSummaryResponse> getSubscribers(String email, Boolean isActive, Pageable pageable) {
        return subscriberRepository.findByFilters(email, isActive, pageable)
                .map(subscriber -> SubscriberSummaryResponse.builder()
                        .id(subscriber.getId())
                        .email(subscriber.getEmail())
                        .isActive(subscriber.getIsActive())
                        .createdAt(subscriber.getCreatedAt())
                        .isUser(subscriber.getUser() != null)
                        .build());
    }

    /**
     * Obtiene los detalles de un suscriptor por su ID.
     * @param id identificador del suscriptor
     * @return detalles del suscriptor encontrado
     * @throws ResourceNotFoundException si no se encuentra un suscriptor con el ID proporcionado
     */
    @Override
    @Transactional(readOnly = true)
    public SubscriberDetailResponse getSubscriberById(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscriptor no encontrado"));

        User user = subscriber.getUser();

        return SubscriberDetailResponse.builder()
                .id(subscriber.getId())
                .email(subscriber.getEmail())
                .isActive(subscriber.getIsActive())
                .createdAt(subscriber.getCreatedAt())
                .updatedAt(subscriber.getUpdatedAt())
                .userId(user != null ? user.getId() : null)
                .firstName(user != null ? user.getFirstName() : null)
                .lastName(user != null ? user.getLastName() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .dni(user != null ? user.getDni() : null)
                .cellphone(user != null ? user.getCellphone() : null)
                .build();
    }

    /**
     * Alterna el estado de suscripción de un suscriptor.
     * Si el suscriptor está activo, lo desactiva. Si está inactivo, lo activa.
     * @param id identificador del suscriptor
     * @throws ResourceNotFoundException si no se encuentra un suscriptor con el ID proporcionado
     */
    @Override
    @Transactional
    public void toggleSubscriberStatus(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscriptor no encontrado"));

        subscriber.setIsActive(!subscriber.getIsActive());
        subscriberRepository.save(subscriber);

        actionLogService.log(ActionType.CAMBIAR_ESTADO, EntityType.SUBSCRIBER, subscriber.getId(),
                "Cambió el estado del suscriptor '" + subscriber.getEmail() + "' a "
                        + (subscriber.getIsActive() ? "ACTIVO" : "INACTIVO"));
    }
}