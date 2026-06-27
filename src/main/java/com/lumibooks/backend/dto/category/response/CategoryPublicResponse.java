package com.lumibooks.backend.dto.category.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO utilizado para exponer categorías.
 */
@Getter
@Builder
public class CategoryPublicResponse {

    private Long id;
    private String name;
    
}
