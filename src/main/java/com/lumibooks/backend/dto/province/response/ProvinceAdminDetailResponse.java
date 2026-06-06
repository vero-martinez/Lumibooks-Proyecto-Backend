package com.lumibooks.backend.dto.province.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa los detalles completos de una provincia para el panel de administración,
 * incluyendo su departamento y lista de distritos.
 */
@Getter
@Builder
public class ProvinceAdminDetailResponse {

    private Long id;
    private String name;
    private String departmentName;
    private Integer districtCount;
    private List<DistrictPublicResponse> districts;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}