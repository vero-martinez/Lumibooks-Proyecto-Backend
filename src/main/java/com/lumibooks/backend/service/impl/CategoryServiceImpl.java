package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.category.request.CategoryRequest;
import com.lumibooks.backend.dto.category.response.CategoryPublicResponse;
import com.lumibooks.backend.dto.category.response.CategorySummaryResponse;
import com.lumibooks.backend.entity.Category;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.CategoryMapper;
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

    // Catálogo público

    /** Lista categorías activas, opcionalmente filtradas por nombre. */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryPublicResponse> getAllActive(String name) {
        List<Category> categories = (name != null && !name.isBlank())
                ? categoryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name)
                : categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(CategoryMapper::toPublicResponse)
                .toList();
    }

    // Administración

    /** Crea una nueva categoría. */
    @Override
    @Transactional
    public CategorySummaryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        Category category = CategoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        actionLogService.log(
                ActionType.CREAR,
                EntityType.CATEGORY,
                saved.getId(),
                "Creó la categoría '" + saved.getName() + "'");
        return CategoryMapper.toSummaryResponse(saved);
    }

    /** Obtiene una categoría por su ID. */
    @Override
    @Transactional(readOnly = true)
    public CategorySummaryResponse getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        return CategoryMapper.toSummaryResponse(category);
    }

    /** Lista categorías con filtros dinámicos por nombre y estado. */
    @Override
    @Transactional(readOnly = true)
    public Page<CategorySummaryResponse> getAll(String name, Boolean isActive, Pageable pageable) {

        Page<Category> categories = categoryRepository.findByFilters(name, isActive, pageable);
        return categories.map(CategoryMapper::toSummaryResponse);
    }

    /** Actualiza los datos de una categoría existente. */
    @Override
    @Transactional
    public CategorySummaryResponse update(Long id, CategoryRequest request) {

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
        return CategoryMapper.toSummaryResponse(updated);
    }

    /** Desactiva una categoría (soft delete). */
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

    /** Activa una categoría previamente desactivada. */
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
}