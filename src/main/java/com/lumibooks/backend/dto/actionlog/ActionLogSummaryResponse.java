package com.lumibooks.backend.dto.actionlog;

import java.time.LocalDate;

import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa las acciones resumidas de los usuarios admin y gestor
 */
@Getter
@Builder
public class ActionLogSummaryResponse {

    private Long id;
    private LocalDate createdAt;
    private String userName;
    private String userRole;
    private ActionType action;
    private EntityType entityName;

}
