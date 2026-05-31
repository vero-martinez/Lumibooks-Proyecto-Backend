package com.lumibooks.backend.dto.district.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    private boolean isActive;
    private String provinceName;
    private String departmentName;
    private BigDecimal shippingCost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}
