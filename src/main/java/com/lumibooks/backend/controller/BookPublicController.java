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

import com.lumibooks.backend.dto.book.response.BookCardResponse;
import com.lumibooks.backend.dto.book.response.BookDetailResponse;
import com.lumibooks.backend.dto.book.response.BookSuggestionResponse;
import com.lumibooks.backend.dto.review.response.ReviewPublicResponse;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;
import com.lumibooks.backend.service.BookService;
import com.lumibooks.backend.service.ReviewService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador público para la consulta de libros.
 * Expone endpoints públicos para la landing, catálogo y detalle de libros
 * activos.
 */
@RestController
@RequestMapping("/api/public/books")
@RequiredArgsConstructor
public class BookPublicController {

    private final BookService bookService;
    private final ReviewService reviewService;

    /** Obtiene los libros más recientes. */
    @GetMapping("/latest")
    public ResponseEntity<List<BookCardResponse>> getLatestBooks() {
        return ResponseEntity.ok(bookService.getLatestBooks());
    }

    /** Obtiene los libros mejor valorados. */
    @GetMapping("/top-rated")
    public ResponseEntity<List<BookCardResponse>> getTopRatedBooks() {
        return ResponseEntity.ok(bookService.getTopRatedBooks());
    }

    /** Lista libros públicos aplicando filtros y paginación. */
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

    /** Devuelve sugerencias de búsqueda de libros. */
    @GetMapping("/suggestions")
    public ResponseEntity<List<BookSuggestionResponse>> getBookSuggestions(
            @RequestParam String search) {

        return ResponseEntity.ok(
                bookService.getBookSuggestions(search));
    }

    /** Obtiene el detalle público de un libro. */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookDetail(id));
    }

    /** Obtiene las reviews de un libro */
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<Page<ReviewPublicResponse>> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam(required = false) Integer rating,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getBookReviews(bookId, rating, pageable));
    }

}