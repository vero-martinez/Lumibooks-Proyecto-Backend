package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.banner.request.BannerCreateRequest;
import com.lumibooks.backend.dto.banner.request.BannerUpdateRequest;
import com.lumibooks.backend.dto.banner.response.BannerAdminDetailResponse;
import com.lumibooks.backend.dto.banner.response.BannerPublicResponse;
import com.lumibooks.backend.dto.banner.response.BannerSummaryResponse;
import com.lumibooks.backend.dto.cloudinary.ImageUploadResponse;
import com.lumibooks.backend.entity.Banner;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.BannerMapper;
import com.lumibooks.backend.repository.BannerRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.BannerService;
import com.lumibooks.backend.service.CloudinaryService;
import com.lumibooks.backend.specification.BannerSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de banners.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;

    private final ActionLogService actionLogService;
    private final CloudinaryService cloudinaryService;

    // ============ Público ============

    @Override
    public List<BannerPublicResponse> getBannersPublic() {
        return bannerRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream() // Convertir a stream para mapear cada Banner a BannerPublicResponse
                .map(bannerMapper::toPublicResponse) // Mapear cada Banner a BannerPublicResponse usando el mapper
                .toList(); // Convertir el stream de BannerPublicResponse a una lista y retornarla
    }

    // ============ Admin ============

    @Override
    public Page<BannerSummaryResponse> getBannersAdmin(String search, Boolean isActive, Pageable pageable) {
        Specification<Banner> spec = Specification.unrestricted();

        // Filtrar por título si se proporciona el parámetro de búsqueda (case-insensitive)
        if (search != null && !search.isBlank()) {
            spec = spec.and(BannerSpecification.titleContains(search));
        }
        // Filtrar por estado activo/inactivo si se proporciona el parámetro
        if (isActive != null) {
            spec = spec.and(BannerSpecification.hasActive(isActive));
        }

        // Ejecutar la consulta con las especificaciones y paginación
        return bannerRepository.findAll(spec, pageable)
                .map(bannerMapper::toSummaryResponse);
    }

    @Override
    public BannerAdminDetailResponse getBannerDetailAdmin(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Banner no encontrado con id: " + id));
        return bannerMapper.toAdminDetailResponse(banner);
    }

    @Override
    @Transactional
    public BannerAdminDetailResponse createBanner(BannerCreateRequest request, MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("La imagen del banner es obligatoria");
        }
        ImageUploadResponse imageResponse = cloudinaryService.uploadImage(image, "lumibooks/banners");
        Banner banner = bannerMapper.toEntity(request);
        banner.setImageUrl(imageResponse.getSecureUrl());
        banner.setImagePublicId(imageResponse.getPublicId());

        Banner saved = bannerRepository.save(banner);

        actionLogService.log(
                ActionType.CREAR,
                EntityType.BANNER,
                saved.getId(),
                "Creó el banner '" + saved.getTitle() + "'");
        return bannerMapper.toAdminDetailResponse(saved);
    }

    @Override
    @Transactional
    public BannerAdminDetailResponse updateBanner(Long id, BannerUpdateRequest request, MultipartFile image) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Banner no encontrado con id: " + id));

        if (image != null && !image.isEmpty()) {
            ImageUploadResponse imageResponse = cloudinaryService.replaceImage(
                    banner.getImagePublicId(),
                    image,
                    "lumibooks/banners");

            banner.setImageUrl(imageResponse.getSecureUrl());
            banner.setImagePublicId(imageResponse.getPublicId());
        }

        boolean seEstaActivando = Boolean.TRUE.equals(request.getIsActive()) && !banner.isActive();
        boolean cambiandoOrden = request.getDisplayOrder() != null
                && !request.getDisplayOrder().equals(banner.getDisplayOrder());

        if (seEstaActivando && request.getDisplayOrder() == null) {
            throw new BadRequestException(
                    "Debes especificar un displayOrder al activar un banner");
        }
        if (seEstaActivando || (banner.isActive() && cambiandoOrden)) {
            Integer ordenAValidar = request.getDisplayOrder() != null
                    ? request.getDisplayOrder()
                    : banner.getDisplayOrder();

            bannerRepository.findByDisplayOrderAndIsActiveTrue(ordenAValidar)
                    .filter(b -> !b.getId().equals(id))
                    .ifPresent(b -> {
                        b.setActive(false);
                        bannerRepository.save(b);
                    });
        }

        bannerMapper.updateEntity(banner, request);

        Banner saved = bannerRepository.save(banner);

        actionLogService.log(
                ActionType.EDITAR,
                EntityType.BANNER,
                saved.getId(),
                "Editó el banner '" + saved.getTitle() + "'");
        return bannerMapper.toAdminDetailResponse(saved);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Banner no encontrado con id: " + id));

        if (banner.isActive()) {
            throw new BadRequestException(
                    "No se puede eliminar un banner activo, desactívalo primero");
        }

        if (banner.getImagePublicId() != null &&
                !banner.getImagePublicId().isBlank()) {
            cloudinaryService.deleteImage(banner.getImagePublicId());
        }

        bannerRepository.delete(banner);

        actionLogService.log(
                ActionType.ELIMINAR,
                EntityType.BANNER,
                banner.getId(),
                "Eliminó el banner '" + banner.getTitle() + "'");
    }

}