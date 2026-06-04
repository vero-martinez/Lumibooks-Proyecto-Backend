package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.District;

/**
 * Especificaciones para consultas dinámicas de distritos, permitiendo filtrar
 * por nombre, estado de actividad, provincia y departamento.
 */
public class DistrictSpecification {

    // Filtrar por nombre (búsqueda parcial, case-insensitive)
    public static Specification<District> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtrar por provincia
    public static Specification<District> hasProvince(Long provinceId) {
        return (root, query, cb) -> cb.equal(root.get("province").get("id"), provinceId);
    }

    // Filtrar por departamento
    public static Specification<District> hasDepartment(Long departmentId) {
        return (root, query, cb) -> cb.equal(root.get("province").get("department").get("id"), departmentId);
    }

}