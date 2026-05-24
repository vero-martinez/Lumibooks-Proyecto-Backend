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

            String searchTerm = "%" + keyword.toLowerCase() + "%";

            var authors = root.join("authors");

            return criteriaBuilder.or(

                    // Título
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            searchTerm),

                    // ISBN
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("isbn")),
                            searchTerm),

                    // Nombre
                    criteriaBuilder.like(
                            criteriaBuilder.lower(authors.get("firstName")),
                            searchTerm),

                    // Apellido
                    criteriaBuilder.like(
                            criteriaBuilder.lower(authors.get("lastName")),
                            searchTerm),

                    // Nombre completo
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.concat(
                                            criteriaBuilder.concat(
                                                    authors.get("firstName"),
                                                    " "),
                                            authors.get("lastName"))),
                            searchTerm));
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