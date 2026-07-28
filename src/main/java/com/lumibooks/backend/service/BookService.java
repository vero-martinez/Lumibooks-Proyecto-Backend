package com.lumibooks.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.book.request.BookCreateRequest;
import com.lumibooks.backend.dto.book.request.BookUpdateRequest;
import com.lumibooks.backend.dto.book.response.BookAdminDetailResponse;
import com.lumibooks.backend.dto.book.response.BookCardResponse;
import com.lumibooks.backend.dto.book.response.BookDetailResponse;
import com.lumibooks.backend.dto.book.response.BookSuggestionResponse;
import com.lumibooks.backend.dto.book.response.BookSummaryResponse;
import com.lumibooks.backend.dto.book.response.BookResponse;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

public interface BookService {

        // Catálogo público

        /** Obtiene los libros más recientes. */
        List<BookCardResponse> getLatestBooks();

        /** Obtiene los libros mejor valorados. */
        List<BookCardResponse> getTopRatedBooks();

        /** Lista libros públicos aplicando filtros y paginación. */
        Page<BookCardResponse> getBooks(
                        String search,
                        Long categoryId,
                        Long publisherId,
                        Long authorId,
                        BookLanguage language,
                        BookFormat format,
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        Pageable pageable);

        /** Devuelve sugerencias de búsqueda de libros. */
        List<BookSuggestionResponse> getBookSuggestions(String search);

        /** Obtiene el detalle público de un libro. */
        BookDetailResponse getBookDetail(Long id);

        // Administración

        /** Lista libros para el panel de administración. */
        Page<BookSummaryResponse> getBooksAdmin(
                        String search,
                        Boolean isActive,
                        BookLanguage language,
                        Pageable pageable);

        /** Obtiene el detalle administrativo de un libro. */
        BookAdminDetailResponse getBookDetailAdmin(Long id);

        // Gestión de libros

        /** Registra un nuevo libro. */
        BookResponse createBook(BookCreateRequest request, MultipartFile coverImage);

        /** Actualiza la información de un libro. */
        BookResponse updateBook(Long id, BookUpdateRequest request, MultipartFile coverImage);

        /** Activa o desactiva un libro. */
        void toggleBookStatus(Long id);

}