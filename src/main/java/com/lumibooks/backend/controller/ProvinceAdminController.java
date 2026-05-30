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

import com.lumibooks.backend.dto.province.request.ProvinceCreateRequest;
import com.lumibooks.backend.dto.province.request.ProvinceUpdateRequest;
import com.lumibooks.backend.dto.province.response.ProvinceAdminDetailResponse;
import com.lumibooks.backend.dto.province.response.ProvinceSummaryResponse;
import com.lumibooks.backend.service.ProvinceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de provincias en el panel de administración.
 */
@RestController
@RequestMapping("/api/admin/provinces")
@RequiredArgsConstructor
public class ProvinceAdminController {

    private final ProvinceService provinceService;

    // Endpoint para obtener la lista de provincias con filtros dinámicos y paginación
    @GetMapping
    public ResponseEntity<Page<ProvinceSummaryResponse>> getProvinces(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long departmentId,
            Pageable pageable) {
        return ResponseEntity.ok(provinceService.getProvincesAdmin(search, isActive, departmentId, pageable));
    }

    // Endpoint para obtener el detalle completo de una provincia por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ProvinceAdminDetailResponse> getProvince(@PathVariable Long id) {
        return ResponseEntity.ok(provinceService.getProvinceDetailAdmin(id));
    }

    // Endpoint para crear una nueva provincia
    @PostMapping
    public ResponseEntity<ProvinceAdminDetailResponse> createProvince(
            @RequestBody @Valid ProvinceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(provinceService.createProvince(request));
    }

    // Endpoint para actualizar una provincia existente
    @PatchMapping("/{id}")
    public ResponseEntity<ProvinceAdminDetailResponse> updateProvince(
            @PathVariable Long id,
            @RequestBody @Valid ProvinceUpdateRequest request) {
        return ResponseEntity.ok(provinceService.updateProvince(id, request));
    }

}
