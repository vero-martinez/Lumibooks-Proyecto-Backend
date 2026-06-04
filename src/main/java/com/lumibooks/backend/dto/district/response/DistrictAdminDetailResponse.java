package com.lumibooks.backend.dto.district.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información detallada de un distrito para el panel de administración,
 * incluye información adicional como el nombre del departamento y la provincia a la que pertenece.
 */
@Getter
@Builder
public class DistrictAdminDetailResponse {

    private Long id;
    private String name;
    private Long provinceId;
    private String provinceName;
    private Long departmentId;
    private String departmentName;
    private BigDecimal shippingCost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}
