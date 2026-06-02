package com.lumibooks.backend.dto.user.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.RoleUser;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa los detalles completos de un usuario para el panel de administración,
 * incluyendo información sobre su suscripción.
 */
@Getter
@Builder
public class UserAdminDetailResponse {

    private Long id;
    private String fullName;
    private String email;
    private String dni;
    private String cellphone;
    private RoleUser role;
    private Boolean isActive;
    private Boolean isSubscribed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}