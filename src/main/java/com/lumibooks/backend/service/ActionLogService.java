package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.actionlog.ActionLogDetailResponse;
import com.lumibooks.backend.dto.actionlog.ActionLogSummaryResponse;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;

import java.time.LocalDate;

public interface ActionLogService {

    // Registrar una acción en el sistema
    void log(ActionType action, EntityType entityName, Long entityId, String description);

    // Obtener logs paginados con filtros dinámicos
    Page<ActionLogSummaryResponse> getActionLogs(
            String search,
            String role,
            EntityType entityName,
            ActionType action,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);

    // Obtener detalle completo de un log
    ActionLogDetailResponse getActionLogDetail(Long id);

}