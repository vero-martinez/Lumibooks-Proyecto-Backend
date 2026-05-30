package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.service.ProvinceService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar las solicitudes relacionadas con las provincias en la parte pública de la aplicación.
 */
@RestController
@RequestMapping("/api/public/provinces")
@RequiredArgsConstructor
public class ProvincePublicController {

    private final ProvinceService provinceService;

    // Endpoint para obtener la lista de provincias activas de un departamento, con opción de búsqueda por nombre
    @GetMapping
    public ResponseEntity<List<ProvincePublicResponse>> getProvinces(
            @RequestParam Long departmentId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(provinceService.getProvincesPublic(departmentId, search));
    }

}
