package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.banner.request.BannerCreateRequest;
import com.lumibooks.backend.dto.banner.request.BannerUpdateRequest;
import com.lumibooks.backend.dto.banner.response.BannerAdminDetailResponse;
import com.lumibooks.backend.dto.banner.response.BannerSummaryResponse;
import com.lumibooks.backend.service.BannerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar las solicitudes relacionadas con los banners en el panel de administración.
 */
@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class BannerAdminController {

    private final BannerService bannerService;

    // Endpoint para obtener banners con filtros dinámicos para la tabla de administración
    @GetMapping
    public ResponseEntity<Page<BannerSummaryResponse>> getBanners(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        return ResponseEntity.ok(bannerService.getBannersAdmin(search, isActive, pageable));
    }

    // Endpoint para obtener el detalle completo de un banner para el panel de administración
    @GetMapping("/{id}")
    public ResponseEntity<BannerAdminDetailResponse> getBanner(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.getBannerDetailAdmin(id));
    }

    // Endpoint para crear un nuevo banner
    @PostMapping
    public ResponseEntity<BannerAdminDetailResponse> createBanner(
            @RequestBody @Valid BannerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bannerService.createBanner(request));
    }

    // Endpoint para actualizar un banner existente
    @PatchMapping("/{id}")
    public ResponseEntity<BannerAdminDetailResponse> updateBanner(
            @PathVariable Long id,
            @RequestBody @Valid BannerUpdateRequest request) {
        return ResponseEntity.ok(bannerService.updateBanner(id, request));
    }

    // Endpoint para eliminar un banner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

}
