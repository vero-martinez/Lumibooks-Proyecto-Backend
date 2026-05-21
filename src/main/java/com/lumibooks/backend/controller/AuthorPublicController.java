package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.response.AuthorPublicResponse;
import com.lumibooks.backend.service.AuthorService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador público para la gestión y consulta de autores.
 * Expone endpoints públicos para listar autores activos y obtener
 * los detalles públicos de un autor específico.
 */
@RestController
@RequestMapping("/api/public/authors")
@RequiredArgsConstructor
public class AuthorPublicController {

    private final AuthorService authorService;

    /**
     * Obtiene una lista paginada de autores activos para la vista pública.
     * Permite filtrar opcionalmente por nombre o apellido.
     * 
     * @param name     filtro opcional para buscar por nombre o apellido.
     * @param pageable información de paginación y ordenamiento.
     * @return página de autores activos visibles públicamente.
     */
    @GetMapping
    public ResponseEntity<Page<AuthorPublicResponse>> getAuthors(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        return ResponseEntity.ok(authorService.getAuthorsPublic(name, pageable));
    }

    /**
     * Obtiene los detalles públicos de un autor por su ID.
     * Solo devuelve información de autores que se encuentren activos.
     * 
     * @param id identificador del autor.
     * @return detalles públicos del autor encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorPublicResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorByIdPublic(id));
    }

}