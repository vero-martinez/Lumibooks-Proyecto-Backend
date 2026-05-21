package com.lumibooks.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    /**
     * Verifica si existe un autor por nombre y apellido ignorando mayúsculas y minúsculas. 
     * @param firstName nombre del autor a verificar
     * @param lastName apellido del autor a verificar
     * @return true si existe un autor con ese nombre y apellido, false si no existe
     */
    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    /**
     * Obtiene una lista paginada de autores aplicando filtros opcionales. 
     * @param name nombre del autor a filtrar
     * @param isActive estado del autor a filtrar
     * @param pageable objeto para manejar la paginación
     * @return lista de autores que cumplen con los filtros
     */
    @Query("""
                SELECT a FROM Author a
                WHERE (:name IS NULL OR a.firstName ILIKE %:name% OR a.lastName ILIKE %:name%)
                AND (:isActive IS NULL OR a.isActive = :isActive)
            """)
    Page<Author> findByFilters(
            @Param("name") String name,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

}
