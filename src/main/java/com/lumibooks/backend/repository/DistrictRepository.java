package com.lumibooks.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.District;

/**
 * Repositorio para la entidad Distrito, extendiendo JpaRepository para operaciones CRUD básicas
 * y JpaSpecificationExecutor para consultas dinámicas con filtros personalizados.
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, Long>, JpaSpecificationExecutor<District> {

    // Verificar nombre único dentro de la misma provincia (al crear)
    boolean existsByNameAndProvinceId(String name, Long provinceId);

    // Verificar nombre único dentro de la misma provincia (al actualizar, excluye
    // el propio distrito)
    boolean existsByNameAndProvinceIdAndIdNot(String name, Long provinceId, Long id);

}
