package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Encontrar un usuario por su email
    Optional<User> findByEmail(String email);

    // Validar que el email y dni no estén duplicados
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);

    // Validar duplicados excluyendo el propio usuario (para update)
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByDniAndIdNot(String dni, Long id);

    // Obtener todos los usuarios activos con un rol específico 
    // (usado para notificar a admin cuando el stock de un libro se agota)
    List<User> findByRoleAndIsActiveTrue(RoleUser role);

}