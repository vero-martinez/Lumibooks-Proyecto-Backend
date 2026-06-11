package com.lumibooks.backend.mapper;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.notification.response.NotificationResponse;
import com.lumibooks.backend.entity.Notification;

/**
 * Mapper encargado de transformar entidades Notification en DTOs de respuesta.
 */
@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}