package com.lumibooks.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Verificar si un libro ya está en el carrito
    boolean existsByCartIdAndBookId(Long cartId, Long bookId);

    // Obtener un item específico del carrito
    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);

    // Eliminar todos los items del carrito (vaciar)
    void deleteByCartId(Long cartId);

}