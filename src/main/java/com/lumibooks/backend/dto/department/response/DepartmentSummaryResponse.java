package com.lumibooks.backend.dto.department.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa un resumen de la información de un departamento,
 * utilizado para mostrar en el panel de administración
 */
@Getter
@Builder
public class DepartmentSummaryResponse {

    private Long id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}