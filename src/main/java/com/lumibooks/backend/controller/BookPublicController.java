package com.lumibooks.backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.response.BookCardResponse;
import com.lumibooks.backend.dto.response.BookDetailResponse;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;
import com.lumibooks.backend.service.BookService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador público para la consulta de libros.
 * Expone endpoints públicos para la landing, catálogo y detalle de libros activos.
 */
@RestController
@RequestMapping("/api/public/books")
@RequiredArgsConstructor
public class BookPublicController {

    private final BookService bookService;

    /**
     * Retorna los 10 libros activos más recientes para la landing.
     *
     * @return lista de hasta 10 libros en formato card
     */
    @GetMapping("/latest")
    public ResponseEntity<List<BookCardResponse>> getLatestBooks() {
        return ResponseEntity.ok(bookService.getLatestBooks());
    }

    /**
     * Retorna libros activos con filtros dinámicos para el catálogo público.
     *
     * @param search      búsqueda por título, ISBN o autor
     * @param categoryId  filtro por categoría
     * @param publisherId filtro por editorial
     * @param language    filtro por idioma
     * @param format      filtro por formato
     * @param minPrice    filtro por precio mínimo
     * @param maxPrice    filtro por precio máximo
     * @param pageable    paginación y ordenamiento
     * @return página de libros en formato card
     */
    @GetMapping
    public ResponseEntity<Page<BookCardResponse>> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long publisherId,
            @RequestParam(required = false) BookLanguage language,
            @RequestParam(required = false) BookFormat format,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooks(
                search, categoryId, publisherId, language, format, minPrice, maxPrice, pageable));
    }

    /**
     * Retorna el detalle completo de un libro activo.
     *
     * @param id identificador del libro
     * @return detalle del libro
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookDetail(id));
    }

}