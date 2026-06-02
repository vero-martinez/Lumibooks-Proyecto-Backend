package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;

/**
 * Especificaciones para consultas dinámicas sobre la entidad User,
 * utilizadas para filtrado y búsqueda avanzada en el repositorio.
 */
public class UserSpecification {

    private UserSpecification() {
    }

    // Buscar por nombre, apellido o nombre completo
    public static Specification<User> search(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("firstName")),
                            searchTerm),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("lastName")),
                            searchTerm),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.concat(
                                            criteriaBuilder.concat(
                                                    root.get("firstName"),
                                                    " "),
                                            root.get("lastName"))),
                            searchTerm));
        };
    }

    // Buscar por DNI exacto
    public static Specification<User> hasDni(String dni) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("dni"), dni);
    }

    // Buscar por email
    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("email")),
                "%" + email.toLowerCase() + "%");
    }

    // Filtrar por rol
    public static Specification<User> hasRole(RoleUser role) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), role);
    }

    // Filtrar por estado activo/inactivo
    public static Specification<User> hasActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

}