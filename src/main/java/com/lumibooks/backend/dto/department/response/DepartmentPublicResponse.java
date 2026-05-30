package com.lumibooks.backend.dto.department.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información pública de un departamento, 
 * utilizada para mostrar en la interfaz de usuario.
 */
@Getter
@Builder
public class DepartmentPublicResponse {

    private Long id;
    private String name;

}
