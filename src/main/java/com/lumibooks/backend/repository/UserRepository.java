package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;

/**
 * Repositorio para acceder y consultar usuarios.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Busca un usuario por su correo electrónico.
    Optional<User> findByEmail(String email);

    // Comprueba si el correo o DNI ya existen.
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);

    // Comprueba duplicados excluyendo al usuario que se está actualizando.
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByDniAndIdNot(String dni, Long id);

    // Obtiene los usuarios activos con un rol específico.
    List<User> findByRoleAndIsActiveTrue(RoleUser role);

}