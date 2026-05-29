package com.lumibooks.backend.dto.banner.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa el detalle completo de un banner,
 * utilizado para mostrar en el panel de administración.
 */
@Getter
@Builder
public class BannerAdminDetailResponse {

    private Long id;
    private String title;
    private String imageUrl;
    private String buttonText;
    private String buttonUrl;
    private Integer displayOrder;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}