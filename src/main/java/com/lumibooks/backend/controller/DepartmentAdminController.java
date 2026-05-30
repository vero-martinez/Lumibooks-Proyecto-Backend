package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.department.request.DepartmentCreateRequest;
import com.lumibooks.backend.dto.department.request.DepartmentUpdateRequest;
import com.lumibooks.backend.dto.department.response.DepartmentAdminDetailResponse;
import com.lumibooks.backend.dto.department.response.DepartmentSummaryResponse;
import com.lumibooks.backend.service.DepartmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar las solicitudes relacionadas con los departamentos en el panel de administración.
 */
@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
public class DepartmentAdminController {

    private final DepartmentService departmentService;

    // Endpoint para obtener departamentos con filtros dinámicos para la tabla de administración
    @GetMapping
    public ResponseEntity<Page<DepartmentSummaryResponse>> getDepartments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        return ResponseEntity.ok(departmentService.getDepartmentsAdmin(search, isActive, pageable));
    }

    // Endpoint para obtener el detalle completo de un departamento para el panel de administración
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentAdminDetailResponse> getDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentDetailAdmin(id));
    }

    // Endpoint para crear un nuevo departamento
    @PostMapping
    public ResponseEntity<DepartmentAdminDetailResponse> createDepartment(
            @RequestBody @Valid DepartmentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.createDepartment(request));
    }

    // Endpoint para actualizar un departamento existente
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentAdminDetailResponse> updateDepartment(
            @PathVariable Long id,
            @RequestBody @Valid DepartmentUpdateRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

}