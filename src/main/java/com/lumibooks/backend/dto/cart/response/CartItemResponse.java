package com.lumibooks.backend.dto.cart.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información de un ítem en el carrito de compras.
 */
@Getter
@Builder
public class CartItemResponse {

    private Long cartItemId;
    private Long bookId;
    private String coverImageUrl;
    private String title;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

}