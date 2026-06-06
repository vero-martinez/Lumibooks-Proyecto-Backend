package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.district.request.DistrictCreateRequest;
import com.lumibooks.backend.dto.district.request.DistrictUpdateRequest;
import com.lumibooks.backend.dto.district.response.DistrictAdminDetailResponse;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictSummaryResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.entity.District;
import com.lumibooks.backend.entity.Province;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre entidades de distrito y sus DTOs
 * correspondientes.
 */
@RequiredArgsConstructor
@Component
public class DistrictMapper {

    // ============ Entity --> Response DTO ============
    public DistrictPublicResponse toPublicResponse(District district) {
        return DistrictPublicResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .build();
    }

    public DistrictSummaryResponse toSummaryResponse(District district) {
        return DistrictSummaryResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .department(DepartmentPublicResponse.builder()
                        .id(district.getProvince().getDepartment().getId())
                        .name(district.getProvince().getDepartment().getName())
                        .build())
                .province(ProvincePublicResponse.builder()
                        .id(district.getProvince().getId())
                        .name(district.getProvince().getName())
                        .build())
                .shippingCost(district.getShippingCost())
                .createdAt(district.getCreatedAt())
                .isShippingAvailable(district.isShippingAvailable())
                .build();
    }

    public DistrictAdminDetailResponse toAdminDetailResponse(District district) {
        return DistrictAdminDetailResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .provinceName(district.getProvince().getName())
                .departmentName(district.getProvince().getDepartment().getName())
                .shippingCost(district.getShippingCost())
                .isShippingAvailable(district.isShippingAvailable())
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
                .build();
    }

    public void updateEntity(District district, DistrictUpdateRequest request) {
        Optional.ofNullable(request.getName()).ifPresent(district::setName);
        Optional.ofNullable(request.getShippingCost()).ifPresent(district::setShippingCost);
        Optional.ofNullable(request.getIsShippingAvailable()).ifPresent(district::setShippingAvailable);
    }

}