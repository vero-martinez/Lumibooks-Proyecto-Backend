package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.review.request.ReviewCreateRequest;
import com.lumibooks.backend.dto.review.request.ReviewUpdateRequest;
import com.lumibooks.backend.dto.review.response.ReviewClientResponse;
import com.lumibooks.backend.dto.review.response.ReviewPendingResponse;
import com.lumibooks.backend.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/client/reviews")
@RequiredArgsConstructor
public class ReviewClientController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewClientResponse>> getMyReviews(Pageable pageable) {
        return ResponseEntity.ok(reviewService.getMyReviews(pageable));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<ReviewPendingResponse>> getPendingReviews(Pageable pageable) {
        return ResponseEntity.ok(reviewService.getPendingReviews(pageable));
    }

    @PostMapping
    public ResponseEntity<ReviewClientResponse> createReview(
            @RequestBody @Valid ReviewCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewClientResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

}