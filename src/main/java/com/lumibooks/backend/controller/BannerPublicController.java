package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.banner.response.BannerPublicResponse;
import com.lumibooks.backend.service.BannerService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar las solicitudes relacionadas con los banners en la interfaz pública.
 */
@RestController
@RequestMapping("/api/public/banners")
@RequiredArgsConstructor
public class BannerPublicController {

    private final BannerService bannerService;

    // Endpoint para obtener los banners activos ordenados por displayOrder para el carrusel público
    @GetMapping
    public ResponseEntity<List<BannerPublicResponse>> getBanners() {
        return ResponseEntity.ok(bannerService.getBannersPublic());
    }

}