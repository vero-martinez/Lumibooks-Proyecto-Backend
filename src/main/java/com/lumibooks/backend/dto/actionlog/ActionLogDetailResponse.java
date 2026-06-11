package com.lumibooks.backend.dto.actionlog;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la accion detallada de cada usuario (admin y gestor) en admin
 */
@Getter
@Builder
public class ActionLogDetailResponse {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    
    private String userName;
    private String userDni;
    private String userRole;
    private ActionType action;
    private EntityType entityName;
    private Long entityId;
    private String description;

}