package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.CategoryRequest;
import com.lumibooks.backend.dto.response.CategoryResponse;
import com.lumibooks.backend.entity.Category;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.CategoryRepository;
import com.lumibooks.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio para la gestión de categorías.
 * Contiene la lógica de negocio para crear, obtener, actualizar
 * y cambiar el estado de las categorías.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Crea una nueva categoría validando que no exista otra con el mismo nombre.
     *
     * @param request datos de la categoría a crear
     * @return categoría creada en formato de respuesta
     */
    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        
        if (categoryRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        Category category = Category.builder()
                .nombre(request.getNombre())
                .activo(true)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    /**
     * Obtiene una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return categoría encontrada
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        return mapToResponse(category);
    }

    /**
     * Obtiene una lista paginada de categorías aplicando filtros opcionales.
     *
     * @param nombre filtro por nombre (opcional)
     * @param activo filtro por estado activo/inactivo (opcional)
     * @param pageable configuración de paginación
     * @return página de categorías filtradas
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAll(String nombre, Boolean activo, Pageable pageable) {
        
        Page<Category> categories = categoryRepository.findByFilters(nombre, activo, pageable);
        return categories.map(this::mapToResponse);
    }

    /**
     * Actualiza los datos de una categoría existente.
     *
     * @param id identificador de la categoría
     * @param request nuevos datos de la categoría
     * @return categoría actualizada
     */
    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        if (!category.getNombre().equalsIgnoreCase(request.getNombre()) &&
            categoryRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        category.setNombre(request.getNombre());
        Category updated = categoryRepository.save(category);
        
        return mapToResponse(updated);
    }

    /**
     * Desactiva una categoría (soft delete).
     *
     * @param id identificador de la categoría
     */
    @Override
    @Transactional
    public void deactivate(Long id) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        if (!category.getActivo()) {
            throw new BadRequestException("La categoría ya está desactivada");
        }

        category.setActivo(false);
        categoryRepository.save(category);
    }

    /**
     * Activa una categoría previamente desactivada.
     *
     * @param id identificador de la categoría
     */
    @Override
    @Transactional
    public void activate(Long id) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        if (category.getActivo()) {
            throw new BadRequestException("La categoría ya está activa");
        }

        category.setActivo(true);
        categoryRepository.save(category);
    }

    /**
     * Convierte una entidad Category a su DTO de respuesta.
     *
     * @param category entidad a convertir
     * @return DTO de respuesta
     */
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .nombre(category.getNombre())
                .activo(category.getActivo())
                .fechaCreacion(category.getFechaCreacion())
                .fechaActualizacion(category.getFechaActualizacion())
                .build();
    }
}