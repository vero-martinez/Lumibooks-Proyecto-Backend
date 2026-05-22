package com.lumibooks.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información básica de un libro
 * utilizada en la vista de tarjetas (cards).
 */
@Getter
@Builder
public class BookCardResponse {

    private Long id;
    private String coverImageUrl;
    private String title;
    private List<String> authors;
    private BigDecimal price;
    
}