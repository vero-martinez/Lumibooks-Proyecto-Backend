package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.publisher.request.PublisherRequest;
import com.lumibooks.backend.dto.publisher.response.PublisherPublicResponse;
import com.lumibooks.backend.dto.publisher.response.PublisherSummaryResponse;

/**
 * Interfaz del servicio para gestionar editoriales.
 * Define las operaciones disponibles para crear, obtener, actualizar y eliminar editoriales.
 */
public interface PublisherService {

        // Catálogo público

        /** Lista todas las editoriales activas. */
        List<PublisherPublicResponse> getAllActive(String name);

        // Administración

        /** Crea una nueva editorial. */
        PublisherSummaryResponse create(PublisherRequest request);

        /** Obtiene una editorial por su ID. */
        PublisherSummaryResponse getById(Long id);

        /** Lista editoriales con filtros dinámicos por nombre y estado. */
        Page<PublisherSummaryResponse> getAll(String name, Boolean isActive, Pageable pageable);

        /** Actualiza los datos de una editorial existente. */
        PublisherSummaryResponse update(Long id, PublisherRequest request);

        /** Desactivar o Activar una editorial */
        void toggleStatus(Long id);
}