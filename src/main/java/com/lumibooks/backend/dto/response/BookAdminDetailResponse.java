package com.lumibooks.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información a detalle completo de un libro
 * utilizada en el panel de administración.
 */
@Getter
@Builder
public class BookAdminDetailResponse {

    private Long id;
    private String coverImageUrl;
    private String title;
    private List<String> authors;
    /**
     * Indica si el libro está disponible (Stock > 0)
     */
    private String description;
    private BigDecimal price;
    private String isbn;
    private Integer pageCount;
    private String publisherName;
    private BookLanguage language;
    private BookFormat format;
    private Short editionYear;
    private List<String> categories;
    private Integer stock;
    private boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}