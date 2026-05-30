package com.lumibooks.backend.dto.province.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa un resumen de la información de una provincia 
 * para mostrar en la tabla del panel de administración.
 */
@Getter
@Builder
public class ProvinceSummaryResponse {

    private Long id;
    private String name;
    private boolean isActive;
    private String departmentName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}
