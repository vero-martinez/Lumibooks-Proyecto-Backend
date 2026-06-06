package com.lumibooks.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.province.request.ProvinceCreateRequest;
import com.lumibooks.backend.dto.province.request.ProvinceUpdateRequest;
import com.lumibooks.backend.dto.province.response.ProvinceAdminDetailResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.dto.province.response.ProvinceSummaryResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.entity.District;
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
                .department(DepartmentPublicResponse.builder()
                        .id(province.getDepartment().getId())
                        .name(province.getDepartment().getName())
                        .build())
                .createdAt(province.getCreatedAt())
                .updatedAt(province.getUpdatedAt())
                .build();
    }

    public ProvinceAdminDetailResponse toAdminDetailResponse(
            Province province,
            List<District> districts) {
        return ProvinceAdminDetailResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .departmentName(province.getDepartment().getName())
                .districtCount(districts.size())
                .districts(districts.stream()
                        .map(d -> DistrictPublicResponse.builder()
                                .id(d.getId())
                                .name(d.getName())
                                .build())
                        .toList())
                .createdAt(province.getCreatedAt())
                .updatedAt(province.getUpdatedAt())
                .build();
    }

    // ============ Request DTO --> Entity =============
    public Province toEntity(ProvinceCreateRequest request, Department department) {
        return Province.builder()
                .name(request.getName())
                .department(department)
                .build();
    }

    public void updateEntity(Province province, ProvinceUpdateRequest request) {
        province.setName(request.getName());
    }

}