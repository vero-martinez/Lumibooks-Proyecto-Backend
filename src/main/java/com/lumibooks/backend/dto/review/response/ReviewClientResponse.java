package com.lumibooks.backend.dto.review.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar las reviews de un cliente en su perfil
 */
@Getter
@Builder
public class ReviewClientResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookCoverImageUrl;
    private Integer rating;
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}