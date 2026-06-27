package com.lumibooks.backend.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lumibooks.backend.dto.publisher.request.PublisherRequest;
import com.lumibooks.backend.dto.publisher.response.PublisherSummaryResponse;
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
public class PublisherAdminController {

    private final PublisherService publisherService;

    /** Crea una nueva editorial. */
    @PostMapping
    public ResponseEntity<PublisherSummaryResponse> create(
            @Valid @RequestBody PublisherRequest request) {

        PublisherSummaryResponse response = publisherService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Obtiene una editorial por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PublisherSummaryResponse> getById(@PathVariable Long id) {

        PublisherSummaryResponse response = publisherService.getById(id);
        return ResponseEntity.ok(response);
    }

    /** Lista editoriales aplicando filtros opcionales y paginación. */
    @GetMapping
    public ResponseEntity<Page<PublisherSummaryResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PublisherSummaryResponse> response = publisherService.getAll(name, isActive, pageable);

        return ResponseEntity.ok(response);
    }

    /** Actualiza una editorial existente. */
    @PutMapping("/{id}")
    public ResponseEntity<PublisherSummaryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PublisherRequest request) {

        PublisherSummaryResponse response = publisherService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /** Desactiva una editorial (soft delete). */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        publisherService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    /** Reactiva una editorial previamente desactivada. */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {

        publisherService.activate(id);

        return ResponseEntity.noContent().build();
    }
}