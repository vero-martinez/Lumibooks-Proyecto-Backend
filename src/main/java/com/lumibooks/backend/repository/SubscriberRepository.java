package com.lumibooks.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository <Subscriber, Long> {
    
    /**
     * Verifica si existe una suscripción por email.
     * @param email el email a verificar
     * @return true si existe una suscripción con ese email, false si no existe
     */
    boolean existsByEmail(String email);

    /**
     * Busca una suscripción por email.
     * @param email el email a buscar
     * @return Optional que contiene la suscripción encontrada o vacío si no se encuentra ninguna
     */
    Optional<Subscriber> findByEmail(String email);

    /**
     * Obtiene una lista paginada de suscriptores aplicando filtros opcionales.
     * @param email el email a filtrar
     * @param isActive el estado de la suscripción a filtrar
     * @param pageable la configuración de paginación
     * @return la página de suscriptores que cumplen con los filtros
     */
    @Query("""
                SELECT s FROM Subscriber s
                LEFT JOIN FETCH s.user u
                WHERE (:email IS NULL OR s.email ILIKE %:email%)
                AND (:isActive IS NULL OR s.isActive = :isActive)
            """)
    Page<Subscriber> findByFilters(
            @Param("email") String email,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

}
