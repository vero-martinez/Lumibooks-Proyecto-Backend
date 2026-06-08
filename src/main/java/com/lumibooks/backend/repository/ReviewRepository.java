package com.lumibooks.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumibooks.backend.entity.Review;
import com.lumibooks.backend.enums.ReviewStatus;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // Verificar si el usuario ya dejó una reseña de ese libro
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    // Reseñas del cliente con book cargado (evita N+1)
    @EntityGraph(attributePaths = { "book" })
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Reseñas con user y book cargados (evita N+1) - usado para público y admin
    @EntityGraph(attributePaths = { "user", "book" })
    Page<Review> findAll(Specification<Review> spec, Pageable pageable);

    // Detalle de una reseña con user, book y authors cargados (evita N+1)
    @Query("""
            SELECT r FROM Review r
            JOIN FETCH r.user
            JOIN FETCH r.book b
            JOIN FETCH b.authors
            WHERE r.id = :id
            """)
    Optional<Review> findByIdWithDetails(@Param("id") Long id);

    // Verificar si el usuario tiene una reseña oculta para ese libro
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReviewStatus status);

    // Reseñas de un libro excluyendo las ocultas
    Page<Review> findByBookIdAndStatusNot(Long bookId, ReviewStatus status, Pageable pageable);

    // Reseñas de un libro filtradas por rating excluyendo las ocultas
    Page<Review> findByBookIdAndRatingAndStatusNot(Long bookId, Integer rating, ReviewStatus status, Pageable pageable);

}