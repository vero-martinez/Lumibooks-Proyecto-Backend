package com.lumibooks.backend.dto.district.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información pública de un distrito, 
 * utilizada para mostrar en la interfaz de usuario.
 */
@Getter
@Builder
public class DistrictPublicResponse {

    private Long id;
    private String name;

}