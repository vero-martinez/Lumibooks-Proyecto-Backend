package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Banner;

/**
 * Repositorio para la entidad Banner, extendiendo JpaRepository para operaciones CRUD básicas
 * y JpaSpecificationExecutor para consultas dinámicas con especificaciones.
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long>, JpaSpecificationExecutor<Banner> {

    // Banners activos ordenados para el carrusel público
    List<Banner> findByIsActiveTrueOrderByDisplayOrderAsc();

    // Verificar si el displayOrder ya está ocupado por otro banner activo
    Optional<Banner> findByDisplayOrderAndIsActiveTrue(Integer displayOrder);

}