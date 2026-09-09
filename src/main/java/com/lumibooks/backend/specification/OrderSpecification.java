package com.lumibooks.backend.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Order;
import com.lumibooks.backend.enums.OrderStatus;

public class OrderSpecification {

    private OrderSpecification() {
    }

    // Buscar por número de orden
    public static Specification<Order> orderNumberContains(String orderNumber) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("orderNumber")),
                "%" + orderNumber.toLowerCase() + "%");
    }

    // Buscar por DNI del cliente
    public static Specification<Order> hasDni(String dni) {
        return (root, query, cb) -> cb.equal(
                root.join("user").get("dni"), dni);
    }

    // Buscar por nombre completo del cliente
    public static Specification<Order> clientNameContains(String name) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.join("user").get("firstName")
                        .as(String.class)),
                "%" + name.toLowerCase() + "%");
    }

    // Búsqueda genérica: OR entre número de orden, DNI y nombre completo del cliente
    public static Specification<Order> hasSearch(String search) {
        return (root, query, cb) -> {
            var userJoin = root.join("user");
            String pattern = "%" + search.toLowerCase().trim() + "%";

            var fullName = cb.lower(
                    cb.concat(
                            cb.concat(userJoin.get("firstName"), " "),
                            userJoin.get("lastName")));

            return cb.or(
                    cb.like(cb.lower(root.get("orderNumber")), pattern),
                    cb.like(userJoin.get("dni"), pattern),
                    cb.like(cb.lower(userJoin.get("firstName")), pattern),
                    cb.like(cb.lower(userJoin.get("lastName")), pattern),
                    cb.like(fullName, pattern));
        };
    }

    // Filtrar por estado
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    // Filtrar por gestor asignado
    public static Specification<Order> hasManager(Long managerId) {
        return (root, query, cb) -> cb.equal(
                root.get("assignedManager").get("id"), managerId);
    }

    // Filtrar por fecha desde
    public static Specification<Order> createdAfter(LocalDate date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(
                root.get("createdAt"),
                date.atStartOfDay());
    }

    // Filtrar por fecha hasta
    public static Specification<Order> createdBefore(LocalDate date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(
                root.get("createdAt"),
                date.atTime(23, 59, 59));
    }

    // Solo órdenes del gestor autenticado
    public static Specification<Order> assignedToManager(Long managerId) {
        return (root, query, cb) -> cb.equal(
                root.get("assignedManager").get("id"), managerId);
    }

}