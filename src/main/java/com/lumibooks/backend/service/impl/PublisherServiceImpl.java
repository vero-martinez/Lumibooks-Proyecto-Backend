package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.PublisherRequest;
import com.lumibooks.backend.dto.response.PublisherResponse;
import com.lumibooks.backend.entity.Publisher;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.PublisherRepository;
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

    /**
     * Crea una nueva editorial validando que no exista otra con el mismo nombre.
     *
     * @param request datos de la editorial a crear
     * @return editorial creada en formato de respuesta
     */
    @Override
    @Transactional
    public PublisherResponse create(PublisherRequest request) {
        
        if (publisherRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una editorial con ese nombre");
        }

        Publisher publisher = Publisher.builder()
                .name(request.getName())
                .isActive(true)
                .build();

        Publisher saved = publisherRepository.save(publisher);
        return mapToResponse(saved);
    }

    /**
     * Obtiene una editorial por su identificador.
     *
     * @param id identificador de la editorial
     * @return editorial encontrada
     */
    @Override
    @Transactional(readOnly = true)
    public PublisherResponse getById(Long id) {
        
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        return mapToResponse(publisher);
    }

    /**
     * Obtiene una lista paginada de editoriales aplicando filtros opcionales.
     *
     * @param name filtro por nombre (opcional)
     * @param isActive filtro por estado activo/inactivo (opcional)
     * @param pageable configuración de paginación
     * @return página de editoriales filtradas
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PublisherResponse> getAll(String name, Boolean isActive, Pageable pageable) {
        
        Page<Publisher> publishers = publisherRepository.findByFilters(name, isActive, pageable);
        return publishers.map(this::mapToResponse);
    }

    /**
     * Actualiza los datos de una editorial existente.
     *
     * @param id identificador de la editorial
     * @param request nuevos datos de la editorial
     * @return editorial actualizada
     */
    @Override
    @Transactional
    public PublisherResponse update(Long id, PublisherRequest request) {
        
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada"));

        if (!publisher.getName().equalsIgnoreCase(request.getName()) &&
            publisherRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una editorial con ese nombre");
        }

        publisher.setName(request.getName());
        Publisher updated = publisherRepository.save(publisher);
        
        return mapToResponse(updated);
    }

    /**
     * Desactiva una editorial (soft delete).
     *
     * @param id identificador de la editorial
     */
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
    }

    /**
     * Activa una editorial previamente desactivada.
     *
     * @param id identificador de la editorial
     */
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
    }

    /**
     * Convierte una entidad Publisher a su DTO de respuesta.
     *
     * @param publisher entidad a convertir
     * @return DTO de respuesta
     */
    private PublisherResponse mapToResponse(Publisher publisher) {
        return PublisherResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .isActive(publisher.getIsActive())
                .createdAt(publisher.getCreatedAt())
                .updatedAt(publisher.getUpdatedAt())
                .build();
    }
}