package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.publisher.request.PublisherRequest;
import com.lumibooks.backend.dto.publisher.response.PublisherPublicResponse;
import com.lumibooks.backend.dto.publisher.response.PublisherSummaryResponse;
import com.lumibooks.backend.entity.Publisher;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.PublisherMapper;
import com.lumibooks.backend.repository.PublisherRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.PublisherService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio para la gestión de editoriales.
 * Contiene la lógica de negocio para crear, obtener, actualizar
 * y cambiar el estado de las editoriales.
 */
@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final ActionLogService actionLogService;

    // Catálogo público

    /** Lista todas las editoriales activas. */
    @Override
    @Transactional(readOnly = true)
    public List<PublisherPublicResponse> getAllActive() {
        return publisherRepository.findByIsActiveTrue()
                .stream()
                .map(PublisherMapper::toPublicResponse)
                .toList();
    }

    // Administración

    /** Crea una nueva editorial. */
    @Override
    @Transactional
    public PublisherSummaryResponse create(PublisherRequest request) {

        if (publisherRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una editorial con ese nombre");
        }

        Publisher publisher = PublisherMapper.toEntity(request);
        Publisher saved = publisherRepository.save(publisher);

        actionLogService.log(ActionType.CREAR, EntityType.PUBLISHER, saved.getId(),
                "Creó la editorial '" + saved.getName() + "'");
        return PublisherMapper.toSummaryResponse(saved);
    }

    /** Obtiene una editorial por su ID. */
    @Override
    @Transactional(readOnly = true)
    public PublisherSummaryResponse getById(Long id) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        return PublisherMapper.toSummaryResponse(publisher);
    }

    /** Lista editoriales con filtros dinámicos por nombre y estado. */
    @Override
    @Transactional(readOnly = true)
    public Page<PublisherSummaryResponse> getAll(String name, Boolean isActive, Pageable pageable) {

        Page<Publisher> publishers = publisherRepository.findByFilters(name, isActive, pageable);
        return publishers.map(PublisherMapper::toSummaryResponse);
    }

    /** Actualiza los datos de una editorial existente. */
    @Override
    @Transactional
    public PublisherSummaryResponse update(Long id, PublisherRequest request) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (!publisher.getName().equalsIgnoreCase(request.getName()) &&
                publisherRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una editorial con ese nombre");
        }

        publisher.setName(request.getName());

        Publisher updated = publisherRepository.save(publisher);
        actionLogService.log(ActionType.EDITAR, EntityType.PUBLISHER, updated.getId(),
                "Editó la editorial '" + updated.getName() + "'");
        return PublisherMapper.toSummaryResponse(updated);
    }

    /** Desactiva una editorial (soft delete). */
    @Override
    @Transactional
    public void deactivate(Long id) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (!publisher.getIsActive()) {
            throw new BadRequestException("La editorial ya está desactivada");
        }

        publisher.setIsActive(false);
        publisherRepository.save(publisher);

        actionLogService.log(ActionType.CAMBIAR_ESTADO, EntityType.PUBLISHER, publisher.getId(),
                "Cambió el estado de la editorial '" + publisher.getName() + "' a INACTIVO");
    }

    /** Activa una editorial previamente desactivada. */
    @Override
    @Transactional
    public void activate(Long id) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (publisher.getIsActive()) {
            throw new BadRequestException("La editorial ya está activa");
        }

        publisher.setIsActive(true);
        publisherRepository.save(publisher);

        actionLogService.log(ActionType.CAMBIAR_ESTADO, EntityType.PUBLISHER, publisher.getId(),
                "Cambió el estado de la editorial '" + publisher.getName() + "' a ACTIVO");
    }
}