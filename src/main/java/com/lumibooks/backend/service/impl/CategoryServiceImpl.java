package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.CategoryRequest;
import com.lumibooks.backend.dto.response.CategoryResponse;
import com.lumibooks.backend.entity.Category;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.CategoryRepository;
import com.lumibooks.backend.service.ActionLogService;
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

    private final ActionLogService actionLogService;

    /**
     * Crea una nueva categoría validando que no exista otra con el mismo nombre.
     *
     * @param request datos de la categoría a crear
     * @return categoría creada en formato de respuesta
     */
    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        Category category = Category.builder()
                .name(request.getName())
                .isActive(true)
                .build();

        Category saved = categoryRepository.save(category);
        actionLogService.log(
                ActionType.CREAR,
                EntityType.CATEGORY,
                saved.getId(),
                "Creó la categoría '" + saved.getName() + "'");
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
     * @param name   filtro por nombre (opcional)
     * @param isActive filtro por estado activo/inactivo (opcional)
     * @param pageable configuración de paginación
     * @return página de categorías filtradas
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAll(String name, Boolean isActive, Pageable pageable) {

        Page<Category> categories = categoryRepository.findByFilters(name, isActive, pageable);
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

        if (!category.getName().equalsIgnoreCase(request.getName()) &&
                categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        category.setName(request.getName());
        Category updated = categoryRepository.save(category);

        actionLogService.log(
                ActionType.EDITAR,
                EntityType.CATEGORY,
                updated.getId(),
                "Editó la categoría '" + updated.getName() + "'");
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

        if (!category.getIsActive()) {
            throw new BadRequestException("La categoría ya está desactivada");
        }

        category.setIsActive(false);
        categoryRepository.save(category);

        actionLogService.log(
                ActionType.CAMBIAR_ESTADO,
                EntityType.CATEGORY,
                category.getId(),
                "Cambió el estado de la categoría '" + category.getName() + "' a INACTIVO");
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

        if (category.getIsActive()) {
            throw new BadRequestException("La categoría ya está activa");
        }

        category.setIsActive(true);
        categoryRepository.save(category);

        actionLogService.log(
                ActionType.CAMBIAR_ESTADO,
                EntityType.CATEGORY,
                category.getId(),
                "Cambió el estado de la categoría '" + category.getName() + "' a ACTIVO");
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
                .name(category.getName())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}