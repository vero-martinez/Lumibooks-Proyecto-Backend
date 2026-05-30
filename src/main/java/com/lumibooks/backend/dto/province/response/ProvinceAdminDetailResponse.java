package com.lumibooks.backend.dto.province.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa los detalles de una provincia para el panel de administración,
 * incluyendo información sobre su departamento asociado.
 */
@Getter
@Builder
public class ProvinceAdminDetailResponse {

    private Long id;
    private String name;
    private boolean isActive;
    private Long departmentId;
    private String departmentName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}
