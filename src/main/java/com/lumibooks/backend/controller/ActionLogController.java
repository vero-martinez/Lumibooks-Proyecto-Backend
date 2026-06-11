package com.lumibooks.backend.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.actionlog.ActionLogDetailResponse;
import com.lumibooks.backend.dto.actionlog.ActionLogSummaryResponse;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.service.ActionLogService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de logs de acción — solo accesible por ADMIN.
 */
@RestController
@RequestMapping("/api/admin/action-logs")
@RequiredArgsConstructor
public class ActionLogController {

    private final ActionLogService actionLogService;

    // Obtener logs paginados con filtros dinámicos
    @GetMapping
    public ResponseEntity<Page<ActionLogSummaryResponse>> getActionLogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) EntityType entityName,
            @RequestParam(required = false) ActionType action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(actionLogService.getActionLogs(
                search, role, entityName, action, fromDate, toDate, pageable));
    }

    // Obtener detalle completo de un log
    @GetMapping("/{id}")
    public ResponseEntity<ActionLogDetailResponse> getActionLogDetail(@PathVariable Long id) {
        return ResponseEntity.ok(actionLogService.getActionLogDetail(id));
    }

}