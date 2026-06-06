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

import com.lumibooks.backend.dto.district.request.DistrictCreateRequest;
import com.lumibooks.backend.dto.district.request.DistrictUpdateRequest;
import com.lumibooks.backend.dto.district.response.DistrictAdminDetailResponse;
import com.lumibooks.backend.dto.district.response.DistrictSummaryResponse;
import com.lumibooks.backend.service.DistrictService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de distritos en el panel de administración.
 */
@RestController
@RequestMapping("/api/admin/districts")
@RequiredArgsConstructor
public class DistrictAdminController {

    private final DistrictService districtService;

    // Endpoint para obtener la lista de distritos en la parte administrativa, con opciones de búsqueda y filtrado
    @GetMapping
    public ResponseEntity<Page<DistrictSummaryResponse>> getDistricts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Boolean isShippingAvailable,
            Pageable pageable) {
        return ResponseEntity.ok(districtService.getDistrictsAdmin(search, provinceId, departmentId, isShippingAvailable, pageable));
    }

    // Endpoint para obtener los detalles de un distrito en la parte administrativa
    @GetMapping("/{id}")
    public ResponseEntity<DistrictAdminDetailResponse> getDistrict(@PathVariable Long id) {
        return ResponseEntity.ok(districtService.getDistrictDetailAdmin(id));
    }

    // Endpoint para crear un nuevo distrito en la parte administrativa
    @PostMapping
    public ResponseEntity<DistrictSummaryResponse> createDistrict(
            @RequestBody @Valid DistrictCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(districtService.createDistrict(request));
    }

    // Endpoint para actualizar un distrito existente en la parte administrativa
    @PatchMapping("/{id}")
    public ResponseEntity<DistrictSummaryResponse> updateDistrict(
            @PathVariable Long id,
            @RequestBody @Valid DistrictUpdateRequest request) {
        return ResponseEntity.ok(districtService.updateDistrict(id, request));
    }

}