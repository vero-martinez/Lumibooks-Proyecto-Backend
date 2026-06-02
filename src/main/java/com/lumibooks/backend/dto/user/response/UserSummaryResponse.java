package com.lumibooks.backend.dto.user.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.RoleUser;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa un resumen de la información de un usuario 
 * para mostrar en la tabla del panel de administración.
 */
@Getter
@Builder
public class UserSummaryResponse {

    private Long id;
    private String fullName;
    private String email;
    private String dni;
    private RoleUser role;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}