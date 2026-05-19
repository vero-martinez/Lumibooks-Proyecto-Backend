package com.lumibooks.backend.repository;


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
     * @param nombre nombre de la categoría a verificar
     * @return true si existe, false si no existe
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Obtiene una lista paginada de categorias aplicando filtros opcionales.
     * Si el nombre es null no se filtra por nombre.
     * Si activo es null no se filtra por estado.
     *
     * @param nombre   filtro por nombre (opcional)
     * @param activo   filtro por estado activo o inactivo (opcional)
     * @param pageable configuración de paginación
     * @return página de categorias filtradas
     */
    @Query("""
                SELECT c FROM Category c
                WHERE (:nombre IS NULL OR c.nombre ILIKE %:nombre%)
                AND (:activo IS NULL OR c.activo = :activo)
            """)
    Page<Category> findByFilters(
            @Param("nombre") String nombre,
            @Param("activo") Boolean activo,
            Pageable pageable);

}
