package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Department;

/**
 * Repositorio para la entidad Department, extendiendo JpaRepository para operaciones CRUD básicas
 * y JpaSpecificationExecutor para consultas dinámicas con especificaciones.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Department> findByIsActiveTrueOrderByNameAsc();

}