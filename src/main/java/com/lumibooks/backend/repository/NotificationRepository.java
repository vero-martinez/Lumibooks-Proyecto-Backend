package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumibooks.backend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Obtener notificaciones de un usuario ordenadas por más recientes
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Contar notificaciones de un usuario
    long countByUserId(Long userId);

    // Contar notificaciones no leídas de un usuario
    long countByUserIdAndIsReadFalse(Long userId);

    // Obtener las notificaciones más antiguas de un usuario (para eliminar cuando se supera el límite)
    @Query("""
            SELECT n FROM Notification n
            WHERE n.user.id = :userId
            ORDER BY n.createdAt ASC
            """)
    List<Notification> findOldestByUserId(@Param("userId") Long userId, Pageable pageable);

    // Eliminar todas las notificaciones de un usuario
    void deleteByUserId(Long userId);

    // Verificar si el usuario tiene una notificación específica
    boolean existsByIdAndUserId(Long id, Long userId);

}