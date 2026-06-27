package com.lumibooks.backend.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

public class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> search(String keyword) {

        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            String searchTerm = "%" + keyword.toLowerCase().trim() + "%";
            String normalizedIsbn = keyword.replaceAll("[^0-9]", "");

            var authors = root.join("authors");

            var predicates = criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            searchTerm),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(authors.get("firstName")),
                            searchTerm),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(authors.get("lastName")),
                            searchTerm),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.concat(
                                            criteriaBuilder.concat(
                                                    authors.get("firstName"),
                                                    " "),
                                            authors.get("lastName"))),
                            searchTerm));

            // Solo comparamos ISBN si el usuario escribió los 13 dígitos completos
            if (normalizedIsbn.length() == 13) {
                predicates = criteriaBuilder.or(
                        predicates,
                        criteriaBuilder.equal(root.get("isbn"), normalizedIsbn));
            }

            return predicates;
        };
    }

    // Filtrar por categoría
    public static Specification<Book> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(
                    root.join("categories").get("id"),
                    categoryId);
        };
    }

    // Filtrar por editorial
    public static Specification<Book> hasPublisher(Long publisherId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("publisher").get("id"),
                publisherId);
    }

    // Filtrar por idioma
    public static Specification<Book> hasLanguage(BookLanguage language) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("language"), language);
    }

    // Filtrar por formato
    public static Specification<Book> hasFormat(BookFormat format) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("format"), format);
    }

    // Precio mínimo
    public static Specification<Book> hasMinPrice(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
                root.get("price"),
                minPrice);
    }

    // Precio máximo
    public static Specification<Book> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(
                root.get("price"),
                maxPrice);
    }

    // Solo libros activos
    public static Specification<Book> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive"));
    }

    // Filtrar por estado (activo/inactivo) — uso exclusivo admin
    public static Specification<Book> hasActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

}