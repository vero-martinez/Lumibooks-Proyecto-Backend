package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Department;

/**
 * Repositorio para la entidad Department.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Verificar nombre único al crear
    boolean existsByName(String name);

    // Verificar nombre único al actualizar (excluye la propia entidad)
    boolean existsByNameAndIdNot(String name, Long id);

    // Búsqueda con paginación y filtrado por nombre (case-insensitive) para administración
    Page<Department> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Búsqueda para el endpoint público, ordenada alfabéticamente
    List<Department> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

}