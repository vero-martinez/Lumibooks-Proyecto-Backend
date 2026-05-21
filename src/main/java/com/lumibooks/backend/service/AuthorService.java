package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.request.AuthorRequest;
import com.lumibooks.backend.dto.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.response.AuthorPublicResponse;
import com.lumibooks.backend.dto.response.AuthorSummaryResponse;

/**
 * Interfaz para el servicio de gestión de autores.
 * Define las operaciones disponibles para administrar autores desde el panel de administración,
 * así como para obtener información de autores desde la parte pública de la aplicación.
 */
public interface AuthorService {

    // ======= ADMIN =======

    /**
     * Obtiene una lista paginada de autores aplicando filtros opcionales.
     * @param name filtro por nombre o apellido (opcional)
     * @param isActive filtro por estado de actividad del autor (opcional)
     * @param pageable parámetros de paginación y ordenamiento
     * @return página de autores que cumplen con los filtros aplicados
     */
    Page<AuthorSummaryResponse> getAuthors(String name, Boolean isActive, Pageable pageable);

    /**
     * Obtiene los detalles de un autor por su ID para la vista de administración.
     * @param id identificador del autor
     * @return detalles del autor encontrado para la vista de administración
     */
    AuthorAdminResponse getAuthorByIdAdmin(Long id);

    /**
     * Crea un nuevo autor a partir de los datos proporcionados en el request.
     * @param authorRequest datos del autor a crear
     * @return detalles del autor creado para la vista de administración
     */
    AuthorAdminResponse createAuthor(AuthorRequest authorRequest);

    /**
     * Actualiza los datos de un autor existente.
     * @param id identificador del autor
     * @param authorRequest datos actualizados del autor
     * @return detalles del autor actualizado para la vista de administración
     */
    AuthorAdminResponse updateAuthor(Long id, AuthorRequest authorRequest);

    /**
     * Alterna el estado de actividad de un autor.
     * @param id identificador del autor
     */
    void toggleAuthorStatus(Long id);

    // ====== PUBLIC =======

    /**
     * Obtiene una lista paginada de autores activos aplicando un filtro opcional por nombre o apellido.
     * @param name filtro por nombre o apellido (opcional)
     * @param pageable parámetros de paginación y ordenamiento
     * @return página de autores activos que cumplen con el filtro aplicado
     */
    Page<AuthorPublicResponse> getAuthorsPublic(String name, Pageable pageable);

    /**
     * Obtiene los detalles de un autor activo por su ID para la vista pública.
     * @param id identificador del autor
     * @return detalles del autor encontrado para la vista pública
     */
    AuthorPublicResponse getAuthorByIdPublic(Long id);

}