package com.lumibooks.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Obtener el carrito del usuario
    Optional<Cart> findByUserId(Long userId);

    // Verificar si el usuario ya tiene un carrito
    boolean existsByUserId(Long userId);

}