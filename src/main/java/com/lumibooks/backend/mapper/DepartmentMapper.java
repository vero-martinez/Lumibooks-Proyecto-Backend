package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.department.request.DepartmentCreateRequest;
import com.lumibooks.backend.dto.department.request.DepartmentUpdateRequest;
import com.lumibooks.backend.dto.department.response.DepartmentAdminDetailResponse;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.department.response.DepartmentSummaryResponse;
import com.lumibooks.backend.entity.Department;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre la entidad Department y sus DTOs relacionados.
 */
@RequiredArgsConstructor
@Component
public class DepartmentMapper {

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
                .build();
    }

    public DepartmentAdminDetailResponse toAdminDetailResponse(Department department) {
        return DepartmentAdminDetailResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    // ============ Request DTO --> Entity =============
    public Department toEntity(DepartmentCreateRequest request) {
        return Department.builder()
                .name(request.getName())
                .build();
    }

    public void updateEntity(Department department, DepartmentUpdateRequest request) {
        Optional.ofNullable(request.getName()).ifPresent(department::setName);
    }

}
