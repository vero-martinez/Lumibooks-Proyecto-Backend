package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.author.request.AuthorCreateRequest;
import com.lumibooks.backend.dto.author.request.AuthorUpdateRequest;
import com.lumibooks.backend.dto.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.response.AuthorSummaryResponse;
import com.lumibooks.backend.service.AuthorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador administrativo para la gestión de autores.
 * Expone endpoints para listar, consultar, crear, actualizar
 * y cambiar el estado de activación de autores.
 */
@RestController
@RequestMapping("/api/admin/authors")
@RequiredArgsConstructor
public class AuthorAdminController {

    private final AuthorService authorService;

    /**
     * Obtiene una lista paginada de autores para administración.
     * Permite filtrar opcionalmente por nombre y estado activo/inactivo.
     * 
     * @param name     filtro opcional para buscar por nombre o apellido.
     * @param isActive filtro opcional para buscar autores activos o inactivos.
     * @param pageable información de paginación y ordenamiento.
     * @return página de autores para la vista administrativa.
     */
    @GetMapping
    public ResponseEntity<Page<AuthorSummaryResponse>> getAuthors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(authorService.getAuthors(name, isActive, pageable));
    }

    /**
     * Obtiene los detalles de un autor por su ID para administración.
     * 
     * @param id identificador del autor.
     * @return detalles completos del autor encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorAdminResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorByIdAdmin(id));
    }

    /**
     * Crea un nuevo autor.
     * Valida los datos recibidos antes de registrar el autor.
     * 
     * @param authorRequest datos del autor a crear.
     * @return detalles del autor creado.
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AuthorAdminResponse> createAuthor(
            @RequestPart("data") @Valid AuthorCreateRequest authorRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(authorRequest, profileImage));
    }

    /**
     * Actualiza los datos de un autor existente.
     * Valida los datos recibidos antes de realizar la actualización.
     * 
     * @param id            identificador del autor.
     * @param authorRequest datos actualizados del autor.
     * @return detalles del autor actualizado.
     */
    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<AuthorAdminResponse> updateAuthor(
            @PathVariable Long id,
            @RequestPart(value = "data", required = false) @Valid AuthorUpdateRequest authorRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        if (authorRequest == null)
            authorRequest = new AuthorUpdateRequest();
        return ResponseEntity.ok(authorService.updateAuthor(id, authorRequest, profileImage));
    }

    /**
     * Cambia el estado de activación de un autor.
     * Si el autor está activo, se desactiva; si está inactivo, se activa.
     * 
     * @param id identificador del autor.
     * @return respuesta sin contenido tras completar la operación.
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleAuthorStatus(@PathVariable Long id) {
        authorService.toggleAuthorStatus(id);
        return ResponseEntity.noContent().build();
    }

}