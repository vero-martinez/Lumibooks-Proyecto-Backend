package com.lumibooks.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Publisher;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

        /**
         * Verifica si existe una editorial por nombre ignorando mayúsculas y
         * minúsculas.
         *
         * @param name nombre de la editorial a verificar
         * @return true si existe, false si no existe
         */
        boolean existsByNameIgnoreCase(String name);

        /**
         * Obtiene una lista paginada de editoriales aplicando filtros opcionales.
         * Si el nombre es null no se filtra por nombre.
         * Si activo es null no se filtra por estado.
         *
         * @param name   filtro por nombre (opcional)
         * @param isActive   filtro por estado activo o inactivo (opcional)
         * @param pageable configuración de paginación
         * @return página de editoriales filtradas
         */
        @Query("""
                            SELECT e FROM Publisher e
                            WHERE (:name IS NULL OR e.name ILIKE %:name%)
                            AND (:isActive IS NULL OR e.isActive = :isActive)
                        """)
        Page<Publisher> findByFilters(
                        @Param("name") String name,
                        @Param("isActive") Boolean isActive,
                        Pageable pageable);
}