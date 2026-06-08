package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Listas de un usuario con items precargados — necesario para calcular itemCount en toResponse (evita N+1)
    @EntityGraph(attributePaths = { "items" })
    List<Wishlist> findByUserId(Long userId);

    // Lista específica de un usuario con items y sus libros/autores precargados — usado en getWishlistDetail (evita N+1)
    @EntityGraph(attributePaths = { "items", "items.book", "items.book.authors" })
    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);

    // Verificar si el usuario ya tiene una lista con ese nombre
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    // Verificar si el usuario ya tiene una lista con ese nombre excluyendo la actual (rename)
    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    // Listas del usuario que contienen un libro específico con items precargados — usado en getBookStatus (evita N+1)
    @EntityGraph(attributePaths = { "items", "items.book" })
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