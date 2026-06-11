package com.lumibooks.backend.dto.notification.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar una notificación al usuario
 */
@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}