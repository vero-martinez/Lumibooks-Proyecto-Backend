package com.lumibooks.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.ActionLog;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, JpaSpecificationExecutor<ActionLog> {

    // Obtener un log por ID con usuario precargado (evita N+1)
    @EntityGraph(attributePaths = { "user" })
    java.util.Optional<ActionLog> findById(Long id);

    // Listado paginado con filtros dinámicos; carga usuario (evita N+1)
    @EntityGraph(attributePaths = { "user" })
    Page<ActionLog> findAll(Specification<ActionLog> spec, Pageable pageable);

}