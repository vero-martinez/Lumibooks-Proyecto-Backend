package com.lumibooks.backend.dto.province.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información pública de una provincia, 
 * utilizada para mostrar en la interfaz de usuario.
 */
@Getter
@Builder
public class ProvincePublicResponse {

    private Long id;
    private String name;

}