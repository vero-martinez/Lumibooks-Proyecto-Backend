package com.lumibooks.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.request.BookCreateRequest;
import com.lumibooks.backend.dto.request.BookUpdateRequest;
import com.lumibooks.backend.dto.response.BookAdminDetailResponse;
import com.lumibooks.backend.dto.response.BookCardResponse;
import com.lumibooks.backend.dto.response.BookDetailResponse;
import com.lumibooks.backend.dto.response.BookResponse;
import com.lumibooks.backend.dto.response.BookSummaryResponse;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

public interface BookService {

    /**
     * Obtiene los 10 libros activos más recientes
     * para mostrar en la página principal.
     */
    List<BookCardResponse> getLatestBooks();

    List<BookCardResponse> getTopRatedBooks();

    /**
     * Obtiene una lista paginada de libros públicos
     * aplicando filtros dinámicos y ordenamiento.
     *
     * @param search búsqueda por título, ISBN o autor
     * @param categoryId id de la categoría
     * @param publisherId id de la editorial
     * @param language idioma del libro
     * @param format formato del libro
     * @param minPrice precio mínimo
     * @param maxPrice precio máximo
     * @param pageable configuración de paginación y ordenamiento
     * @return página de libros públicos
     */
    Page<BookCardResponse> getBooks(
            String search,
            Long categoryId,
            Long publisherId,
            BookLanguage language,
            BookFormat format,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Obtiene el detalle público completo de un libro.
     *
     * @param id id del libro
     * @return detalle del libro
     */
    BookDetailResponse getBookDetail(Long id);

    /**
     * Obtiene una lista paginada de libros para administración
     * aplicando filtros dinámicos.
     *
     * @param search búsqueda por título, ISBN o autor
     * @param isActive estado del libro (activo/inactivo)
     * @param language idioma del libro
     * @param pageable configuración de paginación y ordenamiento
     * @return página de libros para administración
     */
    Page<BookSummaryResponse> getBooksAdmin(
            String search,
            Boolean isActive,
            BookLanguage language,
            Pageable pageable);

    /**
     * Obtiene el detalle completo de un libro
     * para el panel de administración.
     *
     * @param id id del libro
     * @return detalle administrativo del libro
     */
    BookAdminDetailResponse getBookDetailAdmin(Long id);

    /**
     * Registra un nuevo libro en el sistema.
     *
     * @param request datos del libro a crear
     * @return libro creado
     */
    BookResponse createBook(BookCreateRequest request);

    /**
     * Actualiza parcialmente la información de un libro.
     *
     * @param id id del libro
     * @param request datos a actualizar
     * @return libro actualizado
     */
    BookResponse updateBook(Long id, BookUpdateRequest request);

    /**
     * Cambia el estado de un libro
     * entre activo e inactivo.
     *
     * @param id id del libro
     */
    void toggleBookStatus(Long id);

}