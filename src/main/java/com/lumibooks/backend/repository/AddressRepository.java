package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Address;

/**
 * Repositorio para la entidad Address, extendiendo JpaRepository.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Obtener todas las direcciones de un usuario
    List<Address> findByUserId(Long userId);

    // Obtener una dirección específica de un usuario
    Optional<Address> findByIdAndUserId(Long id, Long userId);

    // Contar el número de direcciones de un usuario (para validar límite de 5 direcciones)
    long countByUserId(Long userId);

    // Marcar todas las direcciones de un usuario como no predeterminadas 
    // (para establecer una nueva dirección predeterminada)
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultByUserId(@Param("userId") Long userId);

}