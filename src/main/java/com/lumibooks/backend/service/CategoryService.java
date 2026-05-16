package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.request.CategoryRequest;
import com.lumibooks.backend.dto.response.CategoryResponse;

public interface CategoryService {

    /**
     * Crear una nueva categoría
     */
    CategoryResponse create(CategoryRequest request);

    /**
     * Obtener categoría por ID
     */
    CategoryResponse getById(Long id);

    /**
     * Obtener todas las categorías con filtros dinámicos
     * @param nombre Buscar por nombre (opcional)
     * @param activo Filtrar por estado activo/inactivo (opcional)
     * @param pageable Paginación y ordenamiento
     */
    Page<CategoryResponse> getAll(String nombre, Boolean activo, Pageable pageable);

    /**
     * Actualizar una categoría
     */
    CategoryResponse update(Long id, CategoryRequest request);

    /**
     * Desactivar una categoría (soft delete)
     */
    void deactivate(Long id);

    /**
     * Activar una categoría
     */
    void activate(Long id);
}