package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.request.PublisherRequest;
import com.lumibooks.backend.dto.response.PublisherResponse;

/**
 * Interfaz del servicio para gestionar editoriales.
 * Define las operaciones disponibles para crear, obtener, actualizar y eliminar editoriales.
 */
public interface PublisherService {

    /**
     * Crear una nueva editorial
     */
    PublisherResponse create(PublisherRequest request);

    /**
     * Obtener editorial por ID
     */
    PublisherResponse getById(Long id);

    /**
     * Obtener todas las editoriales con filtros dinámicos
     * @param name Buscar por nombre (opcional)
     * @param isActive Filtrar por estado activo/inactivo (opcional)
     * @param pageable Paginación y ordenamiento
     */
    Page<PublisherResponse> getAll(String name, Boolean isActive, Pageable pageable);

    /**
     * Actualizar una editorial
     */
    PublisherResponse update(Long id, PublisherRequest request);

    /**
     * Desactivar una editorial (soft delete)
     */
    void deactivate(Long id);

    /**
     * Activar una editorial
     */
    void activate(Long id);
}