package com.lumibooks.backend.dto.review.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.ReviewStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar el detalle completo de una reseña en admin
 */
@Getter
@Builder
public class ReviewAdminDetailResponse {

    private Long id;
    private String userName;
    private String userDni;
    private String bookTitle;
    private String bookIsbn;
    private List<String> authors;
    private Integer rating;
    private String comment;
    private ReviewStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}