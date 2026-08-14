package com.lumibooks.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.PasswordResetToken;

/**
 * Repositorio para acceder y consultar códigos de recuperación de contraseña.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Obtiene el código activo más reciente de un usuario.
    Optional<PasswordResetToken> findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(Long userId);

    // Elimina todos los códigos de un usuario (para invalidar los anteriores al solicitar uno nuevo).
    void deleteByUserId(Long userId);

    // Elimina los códigos que ya expiraron.
    void deleteByExpiresAtBefore(LocalDateTime now);
    
}