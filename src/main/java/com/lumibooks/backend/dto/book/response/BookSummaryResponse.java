package com.lumibooks.backend.dto.book.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información básica de un libro
 * utilizada en la tabla de administración.
 */
@Getter
@Builder
public class BookSummaryResponse {

    private Long id;
    private String isbn;
    private String title;
    private List<String> authors;
    private BigDecimal price;
    private Integer stock;
    private boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

}