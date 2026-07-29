package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.category.request.CategoryRequest;
import com.lumibooks.backend.dto.category.response.CategoryPublicResponse;
import com.lumibooks.backend.dto.category.response.CategorySummaryResponse;

public interface CategoryService {

        // Catálogo público

        /** Lista todas las categorías activas. */
        List<CategoryPublicResponse> getAllActive(String name);

        // Administración

        /** Crea una nueva categoría. */
        CategorySummaryResponse create(CategoryRequest request);

        /** Obtiene una categoría por su ID. */
        CategorySummaryResponse getById(Long id);

        /** Lista categorías con filtros dinámicos por nombre y estado. */
        Page<CategorySummaryResponse> getAll(String name, Boolean isActive, Pageable pageable);

        /** Actualiza los datos de una categoría existente. */
        CategorySummaryResponse update(Long id, CategoryRequest request);

        /** Cambia el estado activo/inactivo de una categoría. */
        void toggleStatus(Long id);
}