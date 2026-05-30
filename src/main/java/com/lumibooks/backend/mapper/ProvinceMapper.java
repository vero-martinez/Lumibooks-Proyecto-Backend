package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.province.request.ProvinceCreateRequest;
import com.lumibooks.backend.dto.province.request.ProvinceUpdateRequest;
import com.lumibooks.backend.dto.province.response.ProvinceAdminDetailResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.dto.province.response.ProvinceSummaryResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.entity.Province;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre la entidad Province y sus DTOs relacionados.
 */
@RequiredArgsConstructor
@Component
public class ProvinceMapper {

    // ============ Entity --> Response DTO ============
    public ProvincePublicResponse toPublicResponse(Province province) {
        return ProvincePublicResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .build();
    }

    public ProvinceSummaryResponse toSummaryResponse(Province province) {
        return ProvinceSummaryResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .isActive(province.isActive())
                .departmentName(province.getDepartment().getName())
                .createdAt(province.getCreatedAt())
                .build();
    }

    public ProvinceAdminDetailResponse toAdminDetailResponse(Province province) {
        return ProvinceAdminDetailResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .isActive(province.isActive())
                .departmentId(province.getDepartment().getId())
                .departmentName(province.getDepartment().getName())
                .createdAt(province.getCreatedAt())
                .updatedAt(province.getUpdatedAt())
                .build();
    }

    // ============ Request DTO --> Entity =============
    public Province toEntity(ProvinceCreateRequest request, Department department) {
        return Province.builder()
                .name(request.getName())
                .department(department)
                .isActive(Boolean.TRUE.equals(request.getIsActive()))
                .build();
    }

    public void updateEntity(Province province, ProvinceUpdateRequest request, Department department) {
        Optional.ofNullable(request.getName()).ifPresent(province::setName);
        Optional.ofNullable(department).ifPresent(province::setDepartment);
        Optional.ofNullable(request.getIsActive()).ifPresent(province::setActive);
    }

}