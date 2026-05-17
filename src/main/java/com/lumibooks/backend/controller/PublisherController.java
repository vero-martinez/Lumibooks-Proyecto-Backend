package com.lumibooks.backend.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lumibooks.backend.dto.request.PublisherRequest;
import com.lumibooks.backend.dto.response.PublisherResponse;
import com.lumibooks.backend.service.PublisherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de editoriales.
 * Solo accesible por usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    /**
     * Crea una nueva editorial.
     *
     * @param request datos de la editorial
     * @return editorial creada
     */
    @PostMapping
    public ResponseEntity<PublisherResponse> create(
            @Valid @RequestBody PublisherRequest request) {

        PublisherResponse response = publisherService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene una editorial por ID.
     *
     * @param id identificador de la editorial
     * @return editorial encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getById(@PathVariable Long id) {

        PublisherResponse response = publisherService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista editoriales aplicando filtros opcionales y paginación.
     *
     * @param name   filtro por nombre
     * @param isActive   filtro por estado activo/inactivo
     * @param pageable configuración de paginación
     * @return lista paginada de editoriales
     */
    @GetMapping
    public ResponseEntity<Page<PublisherResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PublisherResponse> response = publisherService.getAll(name, isActive, pageable);

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
    public ResponseEntity<PublisherResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PublisherRequest request) {

        PublisherResponse response = publisherService.update(id, request);
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

        publisherService.deactivate(id);

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

        publisherService.activate(id);

        return ResponseEntity.noContent().build();
    }
}