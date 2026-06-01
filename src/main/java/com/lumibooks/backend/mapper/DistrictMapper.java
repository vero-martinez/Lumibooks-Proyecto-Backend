package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.district.request.DistrictCreateRequest;
import com.lumibooks.backend.dto.district.request.DistrictUpdateRequest;
import com.lumibooks.backend.dto.district.response.DistrictAdminDetailResponse;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictSummaryResponse;
import com.lumibooks.backend.entity.District;
import com.lumibooks.backend.entity.Province;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre entidades de distrito y sus DTOs correspondientes.
 */
@RequiredArgsConstructor
@Component
public class DistrictMapper {

    // ============ Entity --> Response DTO ============
    public DistrictPublicResponse toPublicResponse(District district) {
        return DistrictPublicResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .shippingCost(district.getShippingCost())
                .build();
    }

    public DistrictSummaryResponse toSummaryResponse(District district) {
        return DistrictSummaryResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .isActive(district.isActive())
                .provinceName(district.getProvince().getName())
                .departmentName(district.getProvince().getDepartment().getName())
                .shippingCost(district.getShippingCost())
                .createdAt(district.getCreatedAt())
                .build();
    }

    public DistrictAdminDetailResponse toAdminDetailResponse(District district) {
        return DistrictAdminDetailResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .isActive(district.isActive())
                .provinceId(district.getProvince().getId())
                .provinceName(district.getProvince().getName())
                .departmentId(district.getProvince().getDepartment().getId())
                .departmentName(district.getProvince().getDepartment().getName())
                .shippingCost(district.getShippingCost())
                .createdAt(district.getCreatedAt())
                .updatedAt(district.getUpdatedAt())
                .build();
    }

    // ============ Request DTO --> Entity =============
    public District toEntity(DistrictCreateRequest request, Province province) {
        return District.builder()
                .name(request.getName())
                .province(province)
                .shippingCost(request.getShippingCost())
                .isActive(Boolean.TRUE.equals(request.getIsActive()))
                .build();
    }

    public void updateEntity(District district, DistrictUpdateRequest request, Province province) {
        Optional.ofNullable(request.getName()).ifPresent(district::setName);
        Optional.ofNullable(province).ifPresent(district::setProvince);
        Optional.ofNullable(request.getShippingCost()).ifPresent(district::setShippingCost);
        Optional.ofNullable(request.getIsActive()).ifPresent(district::setActive);
    }

}
