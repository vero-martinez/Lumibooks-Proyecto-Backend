package com.lumibooks.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un código de recuperación de contraseña.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que solicitó la recuperación de contraseña.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Código de recuperación cifrado con BCrypt.
    @Column(name = "code_hash", nullable = false, length = 60)
    private String codeHash;

    // Fecha y hora en la que el código deja de ser válido.
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Indica si el código ya fue utilizado.
    @Builder.Default
    @Column(name = "used", nullable = false)
    private boolean used = false;

    // Número de intentos fallidos de validación del código.
    @Builder.Default
    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    // Fecha de creación del registro.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}