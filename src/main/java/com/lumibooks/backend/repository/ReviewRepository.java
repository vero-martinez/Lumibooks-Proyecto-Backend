package com.lumibooks.backend.repository;

import java.util.List;
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

        // Reseñas paginadas de un usuario con el libro precargado (evita N+1)
        @EntityGraph(attributePaths = { "book" })
        Page<Review> findByUserId(Long userId, Pageable pageable);

        // Reseñas paginadas con filtros dinámicos; carga user y book en el mismo query
        // (evita N+1)
        @EntityGraph(attributePaths = { "user", "book" })
        Page<Review> findAll(Specification<Review> spec, Pageable pageable);

        // Detalle completo de una reseña: carga user, book y autores del libro en un
        // solo query (evita N+1)
        @Query("""
                        SELECT r FROM Review r
                        JOIN FETCH r.user
                        JOIN FETCH r.book b
                        JOIN FETCH b.authors
                        WHERE r.id = :id
                        """)
        Optional<Review> findByIdWithDetails(@Param("id") Long id);

        // Verificar si el usuario tiene una reseña con un estado específico para ese
        // libro
        boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReviewStatus status);

        // Reseñas paginadas de un libro excluyendo las ocultas; carga el usuario de
        // cada reseña (evita N+1)
        @EntityGraph(attributePaths = { "user" })
        Page<Review> findByBookIdAndStatusNot(Long bookId, ReviewStatus status, Pageable pageable);

        // Reseñas paginadas de un libro filtradas por rating excluyendo las ocultas;
        // carga el usuario (evita N+1)
        @EntityGraph(attributePaths = { "user" })
        Page<Review> findByBookIdAndRatingAndStatusNot(Long bookId, Integer rating, ReviewStatus status,
                        Pageable pageable);

        // Promedio de rating y total de reseñas agrupados por libro, excluyendo las
        // ocultas
        @Query("""
                        SELECT r.book.id, AVG(r.rating), COUNT(r)
                        FROM Review r
                        WHERE r.book.id IN :bookIds
                        AND r.status != :status
                        GROUP BY r.book.id
                        """)
        List<Object[]> findRatingStatsByBookIds(
                        @Param("bookIds") List<Long> bookIds,
                        @Param("status") ReviewStatus status);

        // IDs de los 10 libros con mejor promedio de rating excluyendo reseñas ocultas
        @Query("""
                        SELECT r.book.id FROM Review r
                        WHERE r.status != :status
                        GROUP BY r.book.id
                        ORDER BY AVG(r.rating) DESC
                        LIMIT 10
                        """)
        List<Long> findTop10BookIdsByAverageRating(@Param("status") ReviewStatus status);

}