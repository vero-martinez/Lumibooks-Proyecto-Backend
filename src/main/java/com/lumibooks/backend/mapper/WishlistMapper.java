package com.lumibooks.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.wishlist.request.WishlistNameRequest;
import com.lumibooks.backend.dto.wishlist.response.WishlistBookStatusResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistDetailResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistResponse;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.entity.Wishlist;

import lombok.RequiredArgsConstructor;

/**
 * Mapper encargado de transformar entidades Wishlist en DTOs de respuesta
 * y convertir datos de solicitud en entidades Wishlist.
 */
@RequiredArgsConstructor
@Component
public class WishlistMapper {

    private final BookMapper bookMapper;

    // ============ Entity --> Response DTO ============

    public WishlistResponse toResponse(Wishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .name(wishlist.getName())
                .itemCount(wishlist.getItems().size())
                .build();
    }

    public WishlistDetailResponse toDetailResponse(Wishlist wishlist) {
        return WishlistDetailResponse.builder()
                .id(wishlist.getId())
                .name(wishlist.getName())
                .books(wishlist.getItems().stream()
                        .map(item -> bookMapper.toCardResponse(item.getBook()))
                        .toList())
                .build();
    }

    public WishlistBookStatusResponse toBookStatusResponse(Long bookId, List<Wishlist> wishlists) {
        List<WishlistResponse> matchingWishlists = wishlists.stream()
                .filter(w -> w.getItems().stream()
                        .anyMatch(item -> item.getBook().getId().equals(bookId)))
                .map(this::toResponse)
                .toList();

        return WishlistBookStatusResponse.builder()
                .bookId(bookId)
                .inWishlist(!matchingWishlists.isEmpty())
                .wishlists(matchingWishlists)
                .build();
    }

    // ============ Request --> Entity ============

    public Wishlist toEntity(WishlistNameRequest request, User user) {
        return Wishlist.builder()
                .name(request.getName())
                .user(user)
                .build();
    }

    public void renameEntity(WishlistNameRequest request, Wishlist wishlist) {
        wishlist.setName(request.getName());
    }

}