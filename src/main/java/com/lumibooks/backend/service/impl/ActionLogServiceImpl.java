package com.lumibooks.backend.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.actionlog.ActionLogDetailResponse;
import com.lumibooks.backend.dto.actionlog.ActionLogSummaryResponse;
import com.lumibooks.backend.entity.ActionLog;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.ActionLogMapper;
import com.lumibooks.backend.repository.ActionLogRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.specification.ActionLogSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de gestión de logs de acción.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionLogServiceImpl implements ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final ActionLogMapper actionLogMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // Registrar una acción en el sistema
    @Override
    @Transactional
    public void log(ActionType action, EntityType entityName, Long entityId, String description) {
        try {
            ActionLog actionLog = ActionLog.builder()
                    .user(authenticatedUserProvider.getAuthenticatedUser())
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .description(description)
                    .build();
            actionLogRepository.save(actionLog);
        } catch (Exception e) {
            log.error("Error al registrar el log de acción: {}", e.getMessage());
        }
    }

    // Obtener logs paginados con filtros dinámicos
    @Override
    public Page<ActionLogSummaryResponse> getActionLogs(
            String search,
            String role,
            EntityType entityName,
            ActionType action,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        Specification<ActionLog> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(ActionLogSpecification.searchByUserNameOrDni(search));
        }
        if (role != null) {
            spec = spec.and(ActionLogSpecification.hasUserRole(role));
        }
        if (entityName != null) {
            spec = spec.and(ActionLogSpecification.hasEntityType(entityName));
        }
        if (action != null) {
            spec = spec.and(ActionLogSpecification.hasActionType(action));
        }
        if (fromDate != null) {
            spec = spec.and(ActionLogSpecification.fromDate(fromDate));
        }
        if (toDate != null) {
            spec = spec.and(ActionLogSpecification.toDate(toDate));
        }

        return actionLogRepository.findAll(spec, pageable)
                .map(actionLogMapper::toSummaryResponse);
    }

    // Obtener detalle completo de un log
    @Override
    public ActionLogDetailResponse getActionLogDetail(Long id) {
        ActionLog actionLog = actionLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Log no encontrado con id: " + id));
        return actionLogMapper.toDetailResponse(actionLog);
    }

}