package com.lumibooks.backend.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lumibooks.backend.dto.request.EditorialRequest;
import com.lumibooks.backend.dto.response.EditorialResponse;
import com.lumibooks.backend.service.EditorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de editoriales.
 * Solo accesible por usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/editorials")
@RequiredArgsConstructor
public class EditorialController {

    private final EditorialService editorialService;

    /**
     * Crea una nueva editorial.
     *
     * @param request datos de la editorial
     * @return editorial creada
     */
    @PostMapping
    public ResponseEntity<EditorialResponse> create(
            @Valid @RequestBody EditorialRequest request) {

        EditorialResponse response = editorialService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene una editorial por ID.
     *
     * @param id identificador de la editorial
     * @return editorial encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<EditorialResponse> getById(@PathVariable Long id) {

        EditorialResponse response = editorialService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista editoriales aplicando filtros opcionales y paginación.
     *
     * @param nombre   filtro por nombre
     * @param activo   filtro por estado activo/inactivo
     * @param pageable configuración de paginación
     * @return lista paginada de editoriales
     */
    @GetMapping
    public ResponseEntity<Page<EditorialResponse>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<EditorialResponse> response = editorialService.getAll(nombre, activo, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza una editorial existente.
     *
     * @param id      identificador de la editorial
     * @param request nuevos datos
     * @return editorial actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<EditorialResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EditorialRequest request) {

        EditorialResponse response = editorialService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Desactiva una editorial (soft delete).
     *
     * @param id identificador de la editorial
     * @return respuesta vacía
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        editorialService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Reactiva una editorial previamente desactivada.
     *
     * @param id identificador de la editorial
     * @return respuesta vacía
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {

        editorialService.activate(id);

        return ResponseEntity.noContent().build();
    }
}