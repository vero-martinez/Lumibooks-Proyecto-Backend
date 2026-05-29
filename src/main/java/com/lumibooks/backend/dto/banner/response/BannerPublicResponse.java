package com.lumibooks.backend.dto.banner.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información pública de un banner, 
 * utilizada para mostrar en la interfaz de usuario.
 */
@Getter
@Builder
public class BannerPublicResponse {

    private Long id;
    private String title;
    private String imageUrl;
    private String buttonText;
    private String buttonUrl;
    private Integer displayOrder;
    
}
