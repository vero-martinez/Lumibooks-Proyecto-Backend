package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Review;
import com.lumibooks.backend.enums.ReviewStatus;

public class ReviewSpecification {

    private ReviewSpecification() {
    }

    // Buscar por título del libro
    public static Specification<Review> bookTitleContains(String title) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.join("book").get("title")),
                "%" + title.toLowerCase() + "%");
    }

    // Buscar por ISBN del libro
    public static Specification<Review> bookIsbnContains(String isbn) {
        return (root, query, cb) -> cb.like(
                root.join("book").get("isbn"),
                "%" + isbn + "%");
    }

    // Filtrar por calificación
    public static Specification<Review> hasRating(Integer rating) {
        return (root, query, cb) -> cb.equal(root.get("rating"), rating);
    }

    // Filtrar por estado
    public static Specification<Review> hasStatus(ReviewStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

}