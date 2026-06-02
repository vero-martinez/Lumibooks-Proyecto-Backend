package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Wishlist;

/**
 * Repositorio para la entidad Wishlist, extendiendo JpaRepository.
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Obtener todas las listas de un usuario
    List<Wishlist> findByUserId(Long userId);

    // Obtener una lista específica de un usuario
    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);

    // Verificar si el usuario ya tiene una lista con ese nombre
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    // Verificar si el usuario ya tiene una lista con ese nombre excluyendo la actual (rename)
    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    // Obtener listas del usuario que contienen un libro específico (para verificar estado del libro en las listas)
    @Query("""
            SELECT w FROM Wishlist w
            JOIN w.items i
            WHERE w.user.id = :userId
            AND i.book.id = :bookId
            """)
    List<Wishlist> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Contar el número de listas de un usuario (para validar límite de 5 listas)
    long countByUserId(Long userId);

}