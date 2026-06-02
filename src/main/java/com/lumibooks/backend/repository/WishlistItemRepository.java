package com.lumibooks.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.WishlistItem;

/**
 * Repositorio para la entidad WishlistItem, extendiendo JpaRepository.
 */
@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    // Verificar si un libro ya está en una lista
    boolean existsByWishlistIdAndBookId(Long wishlistId, Long bookId);

    // Obtener un item específico de una lista
    Optional<WishlistItem> findByWishlistIdAndBookId(Long wishlistId, Long bookId);

}