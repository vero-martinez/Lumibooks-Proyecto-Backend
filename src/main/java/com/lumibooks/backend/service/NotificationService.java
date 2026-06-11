package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.notification.response.NotificationResponse;
import com.lumibooks.backend.entity.User;

public interface NotificationService {

    // ============ Cliente/Gestor ============
    Page<NotificationResponse> getMyNotifications(Pageable pageable);
    long getUnreadCount();
    void markAsRead(Long notificationId);
    void deleteNotification(Long notificationId);
    void deleteAllNotifications();

    // ============ Sistema ============
    void sendNotification(User user, String title, String message);

}