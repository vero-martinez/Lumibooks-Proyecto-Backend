package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Province;
/**
 * Especificaciones para consultas dinámicas sobre la entidad Province, 
 * utilizadas para filtrado y búsqueda avanzada en el repositorio.
 */
public class ProvinceSpecification {

    public static Specification<Province> nameContains(String search) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    public static Specification<Province> hasActive(boolean isActive) {
        return (root, query, cb) ->
                cb.equal(root.get("isActive"), isActive);
    }

    public static Specification<Province> hasDepartment(Long departmentId) {
        return (root, query, cb) ->
                cb.equal(root.get("department").get("id"), departmentId);
    }

    public static Specification<Province> hasDepartmentActive() {
    return (root, query, cb) ->
            cb.equal(root.get("department").get("isActive"), true);
}

}
