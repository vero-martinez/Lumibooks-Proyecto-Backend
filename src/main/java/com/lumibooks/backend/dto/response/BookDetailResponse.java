package com.lumibooks.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información a detalle de un libro
 * utilizada en la vista pública de detalle del libro.
 */
@Getter
@Builder
public class BookDetailResponse {

    private Long id;
    private String coverImageUrl;
    private String title;
    private List<AuthorPublicResponse> authors;
    /**
     * Indica si el libro está disponible (Stock > 0)
     */
    private boolean available; 
    private String description;
    private BigDecimal price;
    private String isbn;
    private Integer pageCount;
    private String publisherName;
    private BookLanguage language;
    private BookFormat format;
    private Short editionYear;
    private List<String> categories;
    private Double averageRating;
    private Long totalReviews;

}
