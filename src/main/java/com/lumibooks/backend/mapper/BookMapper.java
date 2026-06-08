package com.lumibooks.backend.mapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.request.BookCreateRequest;
import com.lumibooks.backend.dto.request.BookUpdateRequest;
import com.lumibooks.backend.dto.response.AuthorPublicResponse;
import com.lumibooks.backend.dto.response.BookAdminDetailResponse;
import com.lumibooks.backend.dto.response.BookCardResponse;
import com.lumibooks.backend.dto.response.BookDetailResponse;
import com.lumibooks.backend.dto.response.BookResponse;
import com.lumibooks.backend.dto.response.BookSummaryResponse;
import com.lumibooks.backend.dto.response.BookWishlistResponse;
import com.lumibooks.backend.entity.Author;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Category;
import com.lumibooks.backend.entity.Publisher;

import lombok.RequiredArgsConstructor;

/**
 * Mapper encargado de transformar entidades Book en DTOs de respuesta
 * y convertir DTOs de solicitud en entidades Book.
 */
@RequiredArgsConstructor
@Component
public class BookMapper {

    private final AuthorMapper authorMapper;

    // ============ Entity --> Response DTO ============

    public BookCardResponse toCardResponse(Book book, Double averageRating, Long totalReviews) {
        return BookCardResponse.builder()
                .id(book.getId())
                .coverImageUrl(book.getCoverImageUrl())
                .title(book.getTitle())
                .authors(extractAuthorNames(book.getAuthors()))
                .price(book.getPrice())
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

    public BookDetailResponse toDetailResponse(Book book, Double averageRating, Long totalReviews) {
        return BookDetailResponse.builder()
                .id(book.getId())
                .coverImageUrl(book.getCoverImageUrl())
                .title(book.getTitle())
                .authors(extractAuthorPublicResponses(book.getAuthors()))
                .available(isAvailable(book.getStock()))
                .description(book.getDescription())
                .price(book.getPrice())
                .isbn(book.getIsbn())
                .pageCount(book.getPageCount())
                .publisherName(extractPublisherName(book.getPublisher()))
                .language(book.getLanguage())
                .format(book.getFormat())
                .editionYear(book.getEditionYear())
                .categories(extractCategoryNames(book.getCategories()))
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

    public BookSummaryResponse toSummaryResponse(Book book) {
        return BookSummaryResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .authors(extractAuthorNames(book.getAuthors()))
                .price(book.getPrice())
                .stock(book.getStock())
                .isActive(book.isActive())
                .createdAt(book.getCreatedAt())
                .build();
    }

    public BookAdminDetailResponse toAdminDetailResponse(Book book, Double averageRating, Long totalReviews) {
        return BookAdminDetailResponse.builder()
                .id(book.getId())
                .coverImageUrl(book.getCoverImageUrl())
                .title(book.getTitle())
                .authors(extractAuthorNames(book.getAuthors()))
                .description(book.getDescription())
                .price(book.getPrice())
                .isbn(book.getIsbn())
                .pageCount(book.getPageCount())
                .publisherName(extractPublisherName(book.getPublisher()))
                .language(book.getLanguage())
                .format(book.getFormat())
                .editionYear(book.getEditionYear())
                .categories(extractCategoryNames(book.getCategories()))
                .stock(book.getStock())
                .isActive(book.isActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

    public BookWishlistResponse toWishlistResponse(Book book) {
        return BookWishlistResponse.builder()
                .id(book.getId())
                .coverImageUrl(book.getCoverImageUrl())
                .title(book.getTitle())
                .authors(extractAuthorNames(book.getAuthors()))
                .price(book.getPrice())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .coverImageUrl(book.getCoverImageUrl())
                .title(book.getTitle())
                .authors(extractAuthorPublicResponses(book.getAuthors()))
                .description(book.getDescription())
                .price(book.getPrice())
                .isbn(book.getIsbn())
                .pageCount(book.getPageCount())
                .publisherName(extractPublisherName(book.getPublisher()))
                .language(book.getLanguage())
                .format(book.getFormat())
                .editionYear(book.getEditionYear())
                .categories(extractCategoryNames(book.getCategories()))
                .build();
    }

    // ============ Request DTO --> Entity =============

    public Book toEntity(BookCreateRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .coverImageUrl(request.getCoverImageUrl())
                .description(request.getDescription())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .stock(request.getStock())
                .pageCount(request.getPageCount())
                .language(request.getLanguage())
                .format(request.getFormat())
                .editionYear(request.getEditionYear())
                .build();
    }

    public void updateEntity(BookUpdateRequest request, Book book) {
        Optional.ofNullable(request.getTitle()).ifPresent(book::setTitle);
        Optional.ofNullable(request.getCoverImageUrl()).ifPresent(book::setCoverImageUrl);
        Optional.ofNullable(request.getDescription()).ifPresent(book::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(book::setPrice);
        Optional.ofNullable(request.getStock()).ifPresent(book::setStock);
        Optional.ofNullable(request.getPageCount()).ifPresent(book::setPageCount);
        Optional.ofNullable(request.getLanguage()).ifPresent(book::setLanguage);
        Optional.ofNullable(request.getFormat()).ifPresent(book::setFormat);
        Optional.ofNullable(request.getEditionYear()).ifPresent(book::setEditionYear);
    }

    // ============ Helpers privados ===================

    // Mapea los autores a una lista de nombres completos.
    private List<String> extractAuthorNames(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return List.of();
        }
        return authors.stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .toList();
    }

    // Mapea los autores a sus respuestas públicas.
    private List<AuthorPublicResponse> extractAuthorPublicResponses(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return List.of();
        }
        return authors.stream()
                .map(authorMapper::toPublicResponse)
                .toList();
    }

    // Mapea las categorías a una lista de nombres.
    private List<String> extractCategoryNames(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        return categories.stream()
                .map(Category::getName)
                .toList();
    }

    // Mapea la editorial a su nombre.
    private String extractPublisherName(Publisher publisher) {
        return publisher != null ? publisher.getName() : null;
    }

    // Determina la disponibilidad del libro según su stock.
    private boolean isAvailable(Integer stock) {
        return stock != null && stock > 0;
    }

}