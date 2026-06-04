package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Department;

/**
 * Especificaciones para consultas dinámicas sobre la entidad Department, 
 * utilizadas para filtrado y búsqueda avanzada en el repositorio.
 */
public class DepartmentSpecification {

    public static Specification<Department> nameContains(String search) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

}
