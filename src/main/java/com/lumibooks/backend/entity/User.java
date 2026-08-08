package com.lumibooks.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.lumibooks.backend.enums.RoleUser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa a un usuario del sistema.
 * Se almacena en la tabla "users".
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    // Identificador único del usuario.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos personales.
    @Column(name = "first_name", nullable = false, length = 150)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // Contraseña cifrada con BCrypt.
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // Documento Nacional de Identidad.
    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    // Rol utilizado para controlar el acceso a la aplicación.
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUser role;

    @Column(name = "cellphone", length = 9)
    private String cellphone;

    // Indica si el usuario se encuentra activo.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * Versión de los tokens del usuario.
     *
     * Permite invalidar todos los JWT emitidos anteriormente
     * aumentando su valor (por ejemplo, al cerrar todas las sesiones
     * o tras un cambio de contraseña).
     */
    @Builder.Default
    @Column(name = "token_version", nullable = false)
    private Integer tokenVersion = 0;

    // Fecha de creación del registro.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Fecha de la última actualización del registro.
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con la información del suscriptor.
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Subscriber subscriber;

    /**
     * Devuelve el nombre completo del usuario.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

}