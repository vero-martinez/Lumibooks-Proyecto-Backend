package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.notification.response.NotificationResponse;
import com.lumibooks.backend.entity.Notification;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.NotificationMapper;
import com.lumibooks.backend.repository.NotificationRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private static final int MAX_NOTIFICATIONS = 50;

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // ======================== CLIENTE / GESTOR  ========================

    // Método para obtener todas las notificaciones de un usuario
    @Override
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(notificationMapper::toResponse);
    }

    // Método para contar las notificaciones no leídas 
    @Override
    public long getUnreadCount() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    // Método para marcar una notificación como leída
    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Notification notification = findNotificationOrThrow(notificationId, user.getId());
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // Método para eliminar una notificación
    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Notification notification = findNotificationOrThrow(notificationId, user.getId());
        notificationRepository.delete(notification);
    }

    // Método para eliminar todas las notificaciones
    @Override
    @Transactional
    public void deleteAllNotifications() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        notificationRepository.deleteByUserId(user.getId());
    }

    // ======================== SISTEMA ========================

    // Método para que el sistema envíe una notificación al usuario
    @Override
    @Transactional
    public void sendNotification(User user, String title, String message) {
        long total = notificationRepository.countByUserId(user.getId());

        if (total >= MAX_NOTIFICATIONS) {
            // Eliminar la más antigua para hacer espacio
            notificationRepository.findOldestByUserId(user.getId(), 
                    org.springframework.data.domain.PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .ifPresent(notificationRepository::delete);
        }

        notificationRepository.save(Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .build());
    }

    // ======================== HELPERS PRIVAADOS ========================

    private Notification findNotificationOrThrow(Long notificationId, Long userId) {
        return notificationRepository.findById(notificationId)
                .filter(n -> n.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notificación no encontrada con id: " + notificationId));
    }

}