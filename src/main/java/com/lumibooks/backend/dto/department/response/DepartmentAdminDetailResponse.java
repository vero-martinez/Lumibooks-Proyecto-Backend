package com.lumibooks.backend.dto.department.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa los detalles de un departamento para el panel de administración,
 */
@Getter
@Builder
public class DepartmentAdminDetailResponse {

    private Long id;
    private String name;
    private boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}