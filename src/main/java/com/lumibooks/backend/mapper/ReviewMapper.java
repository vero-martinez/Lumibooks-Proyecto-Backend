package com.lumibooks.backend.mapper;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.review.request.ReviewCreateRequest;
import com.lumibooks.backend.dto.review.request.ReviewUpdateRequest;
import com.lumibooks.backend.dto.review.response.ReviewPendingResponse;
import com.lumibooks.backend.dto.review.response.ReviewAdminDetailResponse;
import com.lumibooks.backend.dto.review.response.ReviewClientResponse;
import com.lumibooks.backend.dto.review.response.ReviewPublicResponse;
import com.lumibooks.backend.dto.review.response.ReviewSummaryResponse;
import com.lumibooks.backend.entity.Author;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Review;
import com.lumibooks.backend.entity.User;

/**
 * Mapper encargado de transformar entidades Review en DTOs de respuesta
 * y convertir DTOs de solicitud en entidades Review.
 */
@Component
public class ReviewMapper {

    // ============ Entity --> Response DTO ============

    public ReviewPublicResponse toPublicResponse(Review review) {
        return ReviewPublicResponse.builder()
                .id(review.getId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public ReviewClientResponse toClientResponse(Review review) {
        return ReviewClientResponse.builder()
                .id(review.getId())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .bookIsbn(review.getBook().getIsbn())
                .bookCoverImageUrl(review.getBook().getCoverImageUrl())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public ReviewSummaryResponse toSummaryResponse(Review review) {
        return ReviewSummaryResponse.builder()
                .id(review.getId())
                .userName(review.getUser().getFullName())
                .userDni(review.getUser().getDni())
                .bookTitle(review.getBook().getTitle())
                .rating(review.getRating())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public ReviewAdminDetailResponse toAdminDetailResponse(Review review) {
        return ReviewAdminDetailResponse.builder()
                .id(review.getId())
                .userName(review.getUser().getFullName())
                .userDni(review.getUser().getDni())
                .bookTitle(review.getBook().getTitle())
                .bookIsbn(review.getBook().getIsbn())
                .authors(review.getBook().getAuthors().stream()
                        .map(Author::getFullName)
                        .toList())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public ReviewPendingResponse toReviewPendingResponse(Book book) {
        return ReviewPendingResponse.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .coverImageUrl(book.getCoverImageUrl())
                .build();
    }

    // ============ Request DTO --> Entity ============

    public Review toEntity(ReviewCreateRequest request, User user, Book book) {
        return Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    public void updateEntity(ReviewUpdateRequest request, Review review) {
        review.setComment(request.getComment());
    }

}