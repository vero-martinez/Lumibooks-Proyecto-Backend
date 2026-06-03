package com.lumibooks.backend.dto.cart.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa el estado de un libro en el carrito (si está o no en el carrito).
 */
@Getter
@Builder
public class CartBookStatusResponse {

    private Long bookId;
    private boolean inCart;

}