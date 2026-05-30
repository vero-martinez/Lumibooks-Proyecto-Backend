package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.service.DepartmentService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar las solicitudes relacionadas con los departamentos en la parte pública de la aplicación.
 */
@RestController
@RequestMapping("/api/public/departments")
@RequiredArgsConstructor
public class DepartmentPublicController {

    private final DepartmentService departmentService;

    // Endpoint para obtener la lista de departamentos activos, con opción de búsqueda por nombre
    @GetMapping
    public ResponseEntity<List<DepartmentPublicResponse>> getDepartments(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(departmentService.getDepartmentsPublic(search));
    }

}
