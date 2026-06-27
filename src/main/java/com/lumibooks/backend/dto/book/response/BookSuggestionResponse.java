package com.lumibooks.backend.dto.book.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Dto que representa una sugerencia de un libro en el buscador
 */
@Getter
@Builder
public class BookSuggestionResponse {

    private Long id;
    private String coverImageUrl;
    private String title;
    private String author;
    
}