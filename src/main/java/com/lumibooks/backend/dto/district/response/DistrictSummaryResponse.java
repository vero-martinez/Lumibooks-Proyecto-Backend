package com.lumibooks.backend.dto.district.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa un resumen de la información de un distrito,
 * utilizado para mostrar en la tabla del panel de administración.
 */
@Getter
@Builder
public class DistrictSummaryResponse {

    private Long id;
    private String name;
    private ProvincePublicResponse province;
    private DepartmentPublicResponse department;
    private BigDecimal shippingCost;
    private Boolean isShippingAvailable;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}