package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Verifica si existe una categoría por nombre ignorando mayúsculas y
     * minúsculas.
     *
     * @param name nombre de la categoría a verificar
     * @return true si existe, false si no existe
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Obtiene una lista paginada de categorias aplicando filtros opcionales.
     * Si el nombre es null no se filtra por nombre.
     * Si activo es null no se filtra por estado.
     *
     * @param name     filtro por nombre (opcional)
     * @param isActive filtro por estado activo o inactivo (opcional)
     * @param pageable configuración de paginación
     * @return página de categorias filtradas
     */
    @Query("""
                SELECT c FROM Category c
                WHERE (:name IS NULL OR c.name ILIKE %:name%)
                AND (:isActive IS NULL OR c.isActive = :isActive)
            """)
    Page<Category> findByFilters(
            @Param("name") String name,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<Category> findByIsActiveTrue();

    List<Category> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

}