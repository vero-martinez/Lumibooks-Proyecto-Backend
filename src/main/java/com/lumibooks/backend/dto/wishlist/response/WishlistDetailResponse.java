package com.lumibooks.backend.dto.wishlist.response;

import java.util.List;

import com.lumibooks.backend.dto.book.response.BookWishlistResponse;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la respuesta detallada de una lista de deseos.
 */
@Getter
@Builder
public class WishlistDetailResponse {

    private Long id;
    private String name;
    private List<BookWishlistResponse> books;

}