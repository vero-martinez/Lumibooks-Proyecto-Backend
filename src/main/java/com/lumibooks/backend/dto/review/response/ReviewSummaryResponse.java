package com.lumibooks.backend.dto.review.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.ReviewStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar la información resumida de las reseñas en la tabla de admin
 */
@Getter
@Builder
public class ReviewSummaryResponse {

    private Long id;
    private String userName;
    private String userDni;
    private String bookTitle;
    private Integer rating;
    private ReviewStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}