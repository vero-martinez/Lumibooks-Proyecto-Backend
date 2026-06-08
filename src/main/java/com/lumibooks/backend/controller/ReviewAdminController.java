package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.review.request.ReviewStatusUpdateRequest;
import com.lumibooks.backend.dto.review.response.ReviewAdminDetailResponse;
import com.lumibooks.backend.dto.review.response.ReviewSummaryResponse;
import com.lumibooks.backend.enums.ReviewStatus;
import com.lumibooks.backend.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class ReviewAdminController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewSummaryResponse>> getReviewsAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) ReviewStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsAdmin(search, rating, status, pageable));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewAdminDetailResponse> getReviewDetailAdmin(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewDetailAdmin(reviewId));
    }

    @PatchMapping("/{reviewId}/status")
    public ResponseEntity<Void> updateReviewStatus(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewStatusUpdateRequest request) {
        reviewService.updateReviewStatus(reviewId, request);
        return ResponseEntity.noContent().build();
    }

}