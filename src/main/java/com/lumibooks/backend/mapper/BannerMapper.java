package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.banner.request.BannerCreateRequest;
import com.lumibooks.backend.dto.banner.request.BannerUpdateRequest;
import com.lumibooks.backend.dto.banner.response.BannerAdminDetailResponse;
import com.lumibooks.backend.dto.banner.response.BannerPublicResponse;
import com.lumibooks.backend.dto.banner.response.BannerSummaryResponse;
import com.lumibooks.backend.entity.Banner;

import lombok.RequiredArgsConstructor;

/**
 * Mapper encargado de transformar entidades Banner en DTOs de respuesta
 * y convertir DTOs de solicitud en entidades Banner.
 */
@RequiredArgsConstructor
@Component
public class BannerMapper {

    // ============ Entity --> Response DTO ============
    public BannerPublicResponse toPublicResponse(Banner banner){
        return BannerPublicResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .buttonText(banner.getButtonText())
                .buttonUrl(banner.getButtonUrl())
                .displayOrder(banner.getDisplayOrder())
                .build();
    }

    public BannerSummaryResponse toSummaryResponse(Banner banner){
        return BannerSummaryResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .buttonText(banner.getButtonText())
                .displayOrder(banner.getDisplayOrder())
                .isActive(banner.isActive())
                .createdAt(banner.getCreatedAt())
                .build();
    }

    public BannerAdminDetailResponse toAdminDetailResponse(Banner banner){
        return BannerAdminDetailResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .buttonText(banner.getButtonText())
                .buttonUrl(banner.getButtonUrl())
                .displayOrder(banner.getDisplayOrder())
                .isActive(banner.isActive())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }

    // ============ Request DTO --> Entity =============
    public Banner toEntity(BannerCreateRequest request){
        return Banner.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .buttonText(request.getButtonText())
                .buttonUrl(request.getButtonUrl())
                .displayOrder(request.getDisplayOrder())
                .isActive(request.getIsActive())
                .build();
    }

    public void updateEntity(Banner banner, BannerUpdateRequest request){
        Optional.ofNullable(request.getTitle()).ifPresent(banner::setTitle);
        Optional.ofNullable(request.getImageUrl()).ifPresent(banner::setImageUrl);
        Optional.ofNullable(request.getButtonText()).ifPresent(banner::setButtonText);
        Optional.ofNullable(request.getButtonUrl()).ifPresent(banner::setButtonUrl);
        Optional.ofNullable(request.getDisplayOrder()).ifPresent(banner::setDisplayOrder);
        Optional.ofNullable(request.getIsActive()).ifPresent(banner::setActive);
    }

}
