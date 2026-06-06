package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.District;

/**
 * Repositorio para la entidad District.
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, Long>, JpaSpecificationExecutor<District> {

    // Verificar nombre único dentro de la misma provincia (al crear)
    boolean existsByNameAndProvinceId(String name, Long provinceId);

    // Verificar nombre único dentro de la misma provincia (al actualizar, excluye el propio distrito)
    boolean existsByNameAndProvinceIdAndIdNot(String name, Long provinceId, Long id);

    // Distritos de una provincia ordenados alfabéticamente
    List<District> findByProvinceIdOrderByNameAsc(Long provinceId);

    // Carga province y department en una sola query para evitar N+1
    @EntityGraph(attributePaths = {"province", "province.department"})
    Page<District> findAll(Specification<District> spec, Pageable pageable);

}