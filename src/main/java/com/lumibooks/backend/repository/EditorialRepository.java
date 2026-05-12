package com.lumibooks.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Editorial;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {

        /**
         * Verifica si existe una editorial por nombre ignorando mayúsculas y
         * minúsculas.
         *
         * @param nombre nombre de la editorial a verificar
         * @return true si existe, false si no existe
         */
        boolean existsByNombreIgnoreCase(String nombre);

        /**
         * Obtiene una lista paginada de editoriales aplicando filtros opcionales.
         * Si el nombre es null no se filtra por nombre.
         * Si activo es null no se filtra por estado.
         *
         * @param nombre   filtro por nombre (opcional)
         * @param activo   filtro por estado activo o inactivo (opcional)
         * @param pageable configuración de paginación
         * @return página de editoriales filtradas
         */
        @Query("""
                            SELECT e FROM Editorial e
                            WHERE (:nombre IS NULL OR e.nombre ILIKE %:nombre%)
                            AND (:activo IS NULL OR e.activo = :activo)
                        """)
        Page<Editorial> findByFilters(
                        @Param("nombre") String nombre,
                        @Param("activo") Boolean activo,
                        Pageable pageable);
}