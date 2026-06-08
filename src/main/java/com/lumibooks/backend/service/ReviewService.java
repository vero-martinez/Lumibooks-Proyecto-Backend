package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.review.request.ReviewCreateRequest;
import com.lumibooks.backend.dto.review.request.ReviewStatusUpdateRequest;
import com.lumibooks.backend.dto.review.request.ReviewUpdateRequest;
import com.lumibooks.backend.dto.review.response.ReviewPendingResponse;
import com.lumibooks.backend.dto.review.response.ReviewAdminDetailResponse;
import com.lumibooks.backend.dto.review.response.ReviewClientResponse;
import com.lumibooks.backend.dto.review.response.ReviewPublicResponse;
import com.lumibooks.backend.dto.review.response.ReviewSummaryResponse;
import com.lumibooks.backend.enums.ReviewStatus;

public interface ReviewService {

    // ============ Público ============
    Page<ReviewPublicResponse> getBookReviews(Long bookId, Integer rating, Pageable pageable);

    // ============ Cliente ============
    Page<ReviewClientResponse> getMyReviews(Pageable pageable);
    Page<ReviewPendingResponse> getPendingReviews(Pageable pageable);
    ReviewClientResponse createReview(ReviewCreateRequest request);
    ReviewClientResponse updateReview(Long reviewId, ReviewUpdateRequest request);
    void deleteReview(Long reviewId);

    // ============ Admin ============
    Page<ReviewSummaryResponse> getReviewsAdmin(String search, Integer rating, ReviewStatus status, Pageable pageable);
    ReviewAdminDetailResponse getReviewDetailAdmin(Long reviewId);
    void updateReviewStatus(Long reviewId, ReviewStatusUpdateRequest request);

}