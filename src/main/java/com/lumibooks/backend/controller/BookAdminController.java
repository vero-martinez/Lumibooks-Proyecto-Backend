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

import com.lumibooks.backend.dto.book.request.BookCreateRequest;
import com.lumibooks.backend.dto.book.request.BookUpdateRequest;
import com.lumibooks.backend.dto.book.response.BookAdminDetailResponse;
import com.lumibooks.backend.dto.book.response.BookSummaryResponse;
import com.lumibooks.backend.dto.book.response.BookResponse;
import com.lumibooks.backend.enums.BookLanguage;
import com.lumibooks.backend.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador administrativo para la gestión de libros.
 * Expone endpoints protegidos para crear, editar, consultar y cambiar
 * el estado de los libros desde el panel de administración.
 */
@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class BookAdminController {

    private final BookService bookService;

    /**
     * Retorna libros con filtros dinámicos para la tabla de administración.
     *
     * @param search   búsqueda por título, ISBN o autor
     * @param isActive filtro por estado activo/inactivo
     * @param language filtro por idioma
     * @param pageable paginación y ordenamiento
     * @return página de libros en formato resumen
     */
    @GetMapping
    public ResponseEntity<Page<BookSummaryResponse>> getBooksAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) BookLanguage language,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooksAdmin(search, isActive, language, pageable));
    }

    /**
     * Retorna el detalle completo de un libro para el panel de administración.
     *
     * @param id identificador del libro
     * @return detalle completo del libro
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookAdminDetailResponse> getBookDetailAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookDetailAdmin(id));
    }

    /**
     * Crea un nuevo libro.
     *
     * @param request datos del libro a crear
     * @return detalle del libro creado con status 201
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BookResponse> createBook(
            @RequestPart("data") @Valid BookCreateRequest request,
            @RequestPart("coverImage") MultipartFile coverImage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request, coverImage));
    }

    /**
     * Actualiza parcialmente un libro existente.
     *
     * @param id      identificador del libro
     * @param request campos a actualizar
     * @return detalle del libro actualizado
     */
    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestPart(value = "data", required = false) @Valid BookUpdateRequest request,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        if (request == null)
            request = new BookUpdateRequest();
        return ResponseEntity.ok(bookService.updateBook(id, request, coverImage));
    }

    /**
     * Cambia el estado activo/inactivo de un libro.
     *
     * @param id identificador del libro
     * @return 204 No Content
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleBookStatus(@PathVariable Long id) {
        bookService.toggleBookStatus(id);
        return ResponseEntity.noContent().build();
    }

}