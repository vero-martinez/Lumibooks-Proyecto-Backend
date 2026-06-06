package com.lumibooks.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.department.request.DepartmentRequest;
import com.lumibooks.backend.dto.department.response.DepartmentSummaryResponse;
import com.lumibooks.backend.dto.department.response.DepartmentAdminDetailResponse;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.entity.Department;
import com.lumibooks.backend.entity.Province;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre la entidad Department y sus DTOs relacionados.
 */
@RequiredArgsConstructor
@Component
public class DepartmentMapper {

    private final ProvinceMapper provinceMapper;


    // ============ Entity --> Response DTO ============
    public DepartmentPublicResponse toPublicResponse(Department department) {
        return DepartmentPublicResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    public DepartmentSummaryResponse toSummaryResponse(Department department) {
        return DepartmentSummaryResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    public DepartmentAdminDetailResponse toAdminDetailResponse(
            Department department,
            List<Province> provinces) {
        return DepartmentAdminDetailResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .provinceCount(provinces.size())
                .provinces(provinces.stream()
                        .map(provinceMapper::toPublicResponse)
                        .toList())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    // ============ Request DTO --> Entity =============
    public Department toEntity(DepartmentRequest request) {
        return Department.builder()
                .name(request.getName())
                .build();
    }

    public void updateEntity(Department department, DepartmentRequest request) {
        department.setName(request.getName());
    }

}