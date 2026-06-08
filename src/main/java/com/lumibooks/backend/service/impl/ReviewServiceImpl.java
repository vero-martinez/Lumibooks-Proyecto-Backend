package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.review.request.ReviewCreateRequest;
import com.lumibooks.backend.dto.review.request.ReviewStatusUpdateRequest;
import com.lumibooks.backend.dto.review.request.ReviewUpdateRequest;
import com.lumibooks.backend.dto.review.response.ReviewPendingResponse;
import com.lumibooks.backend.dto.review.response.ReviewAdminDetailResponse;
import com.lumibooks.backend.dto.review.response.ReviewClientResponse;
import com.lumibooks.backend.dto.review.response.ReviewPublicResponse;
import com.lumibooks.backend.dto.review.response.ReviewSummaryResponse;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Review;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.OrderStatus;
import com.lumibooks.backend.enums.ReviewStatus;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.ReviewMapper;
import com.lumibooks.backend.repository.BookRepository;
import com.lumibooks.backend.repository.OrderRepository;
import com.lumibooks.backend.repository.ReviewRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.ReviewService;
import com.lumibooks.backend.specification.ReviewSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // ===================== PUBLICO =============================================

    // ============ Método para obtener todas las reseñas de un libro ============
    @Override
    public Page<ReviewPublicResponse> getBookReviews(Long bookId, Integer rating, Pageable pageable) {
        if (rating != null) {
            return reviewRepository.findByBookIdAndRatingAndStatusNot(
                    bookId, rating, ReviewStatus.OCULTA, pageable)
                    .map(reviewMapper::toPublicResponse);
        }
        return reviewRepository.findByBookIdAndStatusNot(bookId, ReviewStatus.OCULTA, pageable)
                .map(reviewMapper::toPublicResponse);
    }

    // ===================== CLIENTE =============================================

    // ============ Método para obtener todas las reseñas de un cliente ==========
    @Override
    public Page<ReviewClientResponse> getMyReviews(Pageable pageable) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return reviewRepository.findByUserId(user.getId(), pageable)
                .map(reviewMapper::toClientResponse);
    }

    // ======= Método para obtener las reseñas pendientes de un cliente ==========
    @Override
    public Page<ReviewPendingResponse> getPendingReviews(Pageable pageable) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return orderRepository.findBooksWithoutReviewByUserId(
                user.getId(), OrderStatus.ENTREGADO, ReviewStatus.OCULTA, pageable)
                .map(reviewMapper::toReviewPendingResponse);
    }

    // ========================= Método para crear una reseña ====================
    @Override
    @Transactional
    public ReviewClientResponse createReview(ReviewCreateRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Book book = findBookOrThrow(request.getBookId());

        // Validar que el usuario haya comprado y recibido el libro
        validateBookDelivered(user.getId(), book.getId());

        // Validar que no tenga una reseña oculta para ese libro
        if (reviewRepository.existsByUserIdAndBookIdAndStatus(
                user.getId(), book.getId(), ReviewStatus.OCULTA)) {
            throw new BadRequestException(
                    "Tu reseña anterior fue ocultada, no puedes dejar una nueva reseña para este libro");
        }

        // Validar que no haya dejado ya una reseña para ese libro
        if (reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new BadRequestException("Ya dejaste una reseña para este libro");
        }

        Review review = reviewMapper.toEntity(request, user, book);
        return reviewMapper.toClientResponse(reviewRepository.save(review));
    }

    // ============ Método para editar una reseña ================================
    @Override
    @Transactional
    public ReviewClientResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Review review = findReviewByUserOrThrow(reviewId, user.getId());
        reviewMapper.updateEntity(request, review);
        return reviewMapper.toClientResponse(reviewRepository.save(review));
    }

    // ============ Método para eliminar una reseña ==============================
    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Review review = findReviewByUserOrThrow(reviewId, user.getId());
        reviewRepository.delete(review);
    }

    // ======================== ADMIN ============================================

    // ============ Método para obtener todas las reseñas ========================
    @Override
    public Page<ReviewSummaryResponse> getReviewsAdmin(
            String search, Integer rating, ReviewStatus status, Pageable pageable) {

        Specification<Review> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(ReviewSpecification.bookTitleContains(search)
                    .or(ReviewSpecification.bookIsbnContains(search)));
        }
        if (rating != null) {
            spec = spec.and(ReviewSpecification.hasRating(rating));
        }
        if (status != null) {
            spec = spec.and(ReviewSpecification.hasStatus(status));
        }

        return reviewRepository.findAll(spec, pageable)
                .map(reviewMapper::toSummaryResponse);
    }

    // ========= Método para obtener el detalle completo de una reseña ===========
    @Override
    public ReviewAdminDetailResponse getReviewDetailAdmin(Long reviewId) {
        Review review = reviewRepository.findByIdWithDetails(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reseña no encontrada con id: " + reviewId));
        return reviewMapper.toAdminDetailResponse(review);
    }

    // ============ Método para cambiar el estatus de una reseña =================
    @Override
    @Transactional
    public void updateReviewStatus(Long reviewId, ReviewStatusUpdateRequest request) {
        Review review = findReviewOrThrow(reviewId);
        review.setStatus(request.getStatus());
        reviewRepository.save(review);
    }

    // ========================= Helpers privados ================================

    // =============== Obtiene la review o lanza excepción =======================
    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reseña no encontrada con id: " + reviewId));
    }

    // =============== Obtiene el libro o lanza excepción ========================
    private Book findBookOrThrow(Long bookId) {
        return bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + bookId));
    }

    // ========= Obtiene la review del usuario o lanza excepción =================
    private Review findReviewByUserOrThrow(Long reviewId, Long userId) {
        Review review = findReviewOrThrow(reviewId);
        if (!review.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Reseña no encontrada con id: " + reviewId);
        }
        return review;
    }

    // =========== Validar que el libro tiene estatus de ENTREGADO ===============
    private void validateBookDelivered(Long userId, Long bookId) {
        boolean hasDeliveredOrder = orderRepository.existsDeliveredOrderWithBook(
                userId, bookId, OrderStatus.ENTREGADO);
        if (!hasDeliveredOrder) {
            throw new BadRequestException(
                    "Solo puedes reseñar libros que hayas comprado y recibido");
        }
    }

}