package com.lumibooks.backend.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.ActionLog;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;

/**
 * Especificaciones para filtrado dinámico de ActionLog.
 */
public class ActionLogSpecification {

    private ActionLogSpecification() {}

    // Buscar por nombre completo o DNI del usuario
    public static Specification<ActionLog> searchByUserNameOrDni(String search) {
        return (root, query, cb) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("user").get("firstName")), pattern),
                    cb.like(cb.lower(root.get("user").get("lastName")), pattern),
                    cb.like(cb.lower(root.get("user").get("dni")), pattern)
            );
        };
    }

    // Filtrar por rol de usuario (ADMIN / GESTOR)
    public static Specification<ActionLog> hasUserRole(String role) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("role"), role);
    }

    // Filtrar por tipo de entidad (LIBRO, USUARIO, etc.)
    public static Specification<ActionLog> hasEntityType(EntityType entityType) {
        return (root, query, cb) ->
                cb.equal(root.get("entityName"), entityType);
    }

    // Filtrar por tipo de acción (CREAR, EDITAR, etc.)
    public static Specification<ActionLog> hasActionType(ActionType actionType) {
        return (root, query, cb) ->
                cb.equal(root.get("action"), actionType);
    }

    // Filtrar desde una fecha (inicio del día)
    public static Specification<ActionLog> fromDate(LocalDate date) {
        return (root, query, cb) -> {
            LocalDateTime start = date.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
        };
    }

    // Filtrar hasta una fecha (fin del día)
    public static Specification<ActionLog> toDate(LocalDate date) {
        return (root, query, cb) -> {
            LocalDateTime end = date.atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("createdAt"), end);
        };
    }

}