package com.lumibooks.backend.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lumibooks.backend.dto.request.CategoryRequest;
import com.lumibooks.backend.dto.response.CategoryResponse;
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
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Crea una nueva categoría.
     *
     * @param request datos de la categoría
     * @return categoría creada
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene una categoría por ID.
     *
     * @param id identificador de la categoría
     * @return categoría encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {

        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista categorías aplicando filtros opcionales y paginación.
     *
     * @param nombre   filtro por nombre
     * @param activo   filtro por estado activo/inactivo
     * @param pageable configuración de paginación
     * @return lista paginada de categorías
     */
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CategoryResponse> response = categoryService.getAll(nombre, activo, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param id      identificador de la categoría
     * @param request nuevos datos
     * @return categoría actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Desactiva una categoría (soft delete).
     *
     * @param id identificador de la categoría
     * @return respuesta vacía
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        categoryService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Reactiva una categoría previamente desactivada.
     *
     * @param id identificador de la categoría
     * @return respuesta vacía
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {

        categoryService.activate(id);

        return ResponseEntity.noContent().build();
    }
}