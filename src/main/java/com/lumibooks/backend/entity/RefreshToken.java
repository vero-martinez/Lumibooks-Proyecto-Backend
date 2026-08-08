package com.lumibooks.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un Refresh Token almacenado en la base de datos.
 *
 * Permite controlar la renovación de sesiones y detectar el reuso
 * de refresh tokens para mayor seguridad.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    // Identificador único del registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario propietario del refresh token.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Hash del Refresh Token.
     * El token real nunca se almacena en la base de datos.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /**
     * Identificador compartido por todos los refresh tokens
     * pertenecientes a la misma sesión (familia).
     */
    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    /**
     * Hash del nuevo Refresh Token que reemplazó a este.
     * Se utiliza para detectar el reuso de tokens antiguos.
     */
    @Column(name = "replaced_by", length = 64)
    private String replacedBy;

    // Indica si el refresh token fue revocado.
    @Builder.Default
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    // Fecha de expiración del refresh token.
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Fecha de creación del registro.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}