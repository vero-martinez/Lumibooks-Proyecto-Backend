package com.lumibooks.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Encontrar un usuario por su email
    Optional<User> findByEmail(String email);

    // Validar que el email y dni no estén duplicados
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);

}
