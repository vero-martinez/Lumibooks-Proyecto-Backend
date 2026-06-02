package com.lumibooks.backend.dto.wishlist.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa el estado de un libro en relación a las listas de deseos del usuario.
 * Contiene el ID del libro, un booleano que indica si el libro está en alguna lista.
 */
@Getter
@Builder
public class WishlistBookStatusResponse {

    private Long bookId;
    private boolean inWishlist;
    private List<WishlistResponse> wishlists;

}