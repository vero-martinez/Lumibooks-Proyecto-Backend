package com.lumibooks.backend.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lumibooks.backend.dto.category.request.CategoryRequest;
import com.lumibooks.backend.dto.category.response.CategorySummaryResponse;
import com.lumibooks.backend.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de categorías.
 * Solo accesible por usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    /** Crea una nueva categoría. */
    @PostMapping
    public ResponseEntity<CategorySummaryResponse> create(
            @Valid @RequestBody CategoryRequest request) {

        CategorySummaryResponse response = categoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Obtiene una categoría por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CategorySummaryResponse> getById(@PathVariable Long id) {

        CategorySummaryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    /** Lista categorías aplicando filtros opcionales y paginación. */
    @GetMapping
    public ResponseEntity<Page<CategorySummaryResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CategorySummaryResponse> response = categoryService.getAll(name, isActive, pageable);

        return ResponseEntity.ok(response);
    }

    /** Actualiza una categoría existente. */
    @PutMapping("/{id}")
    public ResponseEntity<CategorySummaryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategorySummaryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /** Desactiva una categoría (soft delete). */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        categoryService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    /** Reactiva una categoría previamente desactivada. */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {

        categoryService.activate(id);

        return ResponseEntity.noContent().build();
    }
}