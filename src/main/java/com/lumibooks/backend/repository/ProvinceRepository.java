package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Province;

/**
 * Repositorio para la entidad Province, extendiendo JpaRepository para
 * operaciones CRUD básicas
 * y JpaSpecificationExecutor para consultas dinámicas con especificaciones.
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long>, JpaSpecificationExecutor<Province> {

    // Verificar nombre único dentro del mismo departamento (al crear)
    boolean existsByNameAndDepartmentId(String name, Long departmentId);

    // Verificar nombre único dentro del mismo departamento (al actualizar, excluye
    // la propia provincia)
    boolean existsByNameAndDepartmentIdAndIdNot(String name, Long departmentId, Long id);

    // Provincias activas de un departamento ordenadas alfabéticamente (endpoint
    // público)
    List<Province> findByDepartmentIdAndIsActiveTrueOrderByNameAsc(Long departmentId);

    // Desactivar todas las provincias de un departamento
    @Modifying
    @Query("UPDATE Province p SET p.isActive = false WHERE p.department.id = :departmentId")
    void deactivateByDepartmentId(@Param("departmentId") Long departmentId);

}
