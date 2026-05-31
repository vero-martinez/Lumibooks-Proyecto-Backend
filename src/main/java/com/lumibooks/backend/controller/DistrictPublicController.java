package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.service.DistrictService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar las solicitudes relacionadas con los 
 * distritos en la parte pública de la aplicación.
 */
@RestController
@RequestMapping("/api/public/districts")
@RequiredArgsConstructor
public class DistrictPublicController {

    private final DistrictService districtService;

    // Endpoint para obtener la lista de distritos activos de una provincia, con opción de búsqueda por nombre
    @GetMapping
    public ResponseEntity<List<DistrictPublicResponse>> getDistricts(
            @RequestParam Long provinceId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(districtService.getDistrictsPublic(provinceId, search));
    }

}