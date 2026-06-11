package com.lumibooks.backend.mapper;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.actionlog.ActionLogDetailResponse;
import com.lumibooks.backend.dto.actionlog.ActionLogSummaryResponse;
import com.lumibooks.backend.entity.ActionLog;

/**
 * Mapper encargado de transformar entidades ActionLog en DTOs de respuesta.
 */
@Component
public class ActionLogMapper {

    // ============ Entity --> Response DTO ============

    public ActionLogSummaryResponse toSummaryResponse(ActionLog log) {
        return ActionLogSummaryResponse.builder()
                .id(log.getId())
                .createdAt(log.getCreatedAt().toLocalDate())
                .userName(log.getUser().getFullName())
                .userRole(log.getUser().getRole().name())
                .action(log.getAction())
                .entityName(log.getEntityName())
                .build();
    }

    public ActionLogDetailResponse toDetailResponse(ActionLog log) {
        return ActionLogDetailResponse.builder()
                .id(log.getId())
                .createdAt(log.getCreatedAt())
                .userName(log.getUser().getFullName())
                .userDni(log.getUser().getDni())
                .userRole(log.getUser().getRole().name())
                .action(log.getAction())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .build();
    }

}