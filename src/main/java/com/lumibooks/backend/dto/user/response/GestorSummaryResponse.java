package com.lumibooks.backend.dto.user.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa un resumen básico de un gestor activo,
 * para usarse en selectores (p.ej. asignación de gestor a una orden).
 */
@Getter
@Builder
public class GestorSummaryResponse {

    private Long id;
    private String fullName;

}