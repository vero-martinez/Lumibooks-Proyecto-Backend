package com.lumibooks.backend.dto.review.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar la información de libros pendientes de reseña
 */
@Getter
@Builder
public class ReviewPendingResponse {

    private Long bookId;
    private String title;
    private String isbn;
    private String coverImageUrl;
    
}