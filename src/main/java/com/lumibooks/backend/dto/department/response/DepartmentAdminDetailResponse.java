package com.lumibooks.backend.dto.department.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para la respuesta detallada de un departamento en el panel de administración.
 */
@Getter
@Builder
public class DepartmentAdminDetailResponse {

    private Long id;
    private String name;
    private List<ProvincePublicResponse> provinces;
    private Integer provinceCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}