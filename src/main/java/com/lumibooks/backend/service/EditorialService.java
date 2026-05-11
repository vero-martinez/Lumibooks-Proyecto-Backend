package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.request.EditorialRequest;
import com.lumibooks.backend.dto.response.EditorialResponse;

/**
 * Interfaz del servicio para gestionar editoriales.
 * Define las operaciones disponibles para crear, obtener, actualizar y eliminar editoriales.
 */
public interface EditorialService {

    /**
     * Crear una nueva editorial
     */
    EditorialResponse create(EditorialRequest request);

    /**
     * Obtener editorial por ID
     */
    EditorialResponse getById(Long id);

    /**
     * Obtener todas las editoriales con filtros dinámicos
     * @param nombre Buscar por nombre (opcional)
     * @param activo Filtrar por estado activo/inactivo (opcional)
     * @param pageable Paginación y ordenamiento
     */
    Page<EditorialResponse> getAll(String nombre, Boolean activo, Pageable pageable);

    /**
     * Actualizar una editorial
     */
    EditorialResponse update(Long id, EditorialRequest request);

    /**
     * Desactivar una editorial (soft delete)
     */
    void deactivate(Long id);

    /**
     * Activar una editorial
     */
    void activate(Long id);
}