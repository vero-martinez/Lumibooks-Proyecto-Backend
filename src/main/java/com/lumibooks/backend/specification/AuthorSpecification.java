package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Author;

public class AuthorSpecification {

    private AuthorSpecification() {
    }

    // Buscar por nombre completo (nombre y/o apellido)
    public static Specification<Author> search(String keyword) {
        return (root, query, criteriaBuilder) -> {

            String searchTerm = "%" + keyword.toLowerCase().trim() + "%";

            var fullName = criteriaBuilder.lower(
                    criteriaBuilder.concat(
                            criteriaBuilder.concat(root.get("firstName"), " "),
                            root.get("lastName")));

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchTerm),
                    criteriaBuilder.like(fullName, searchTerm));
        };
    }

    // Filtrar por estado (activo/inactivo)
    public static Specification<Author> hasActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    // Solo autores activos
    public static Specification<Author> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive"));
    }

}