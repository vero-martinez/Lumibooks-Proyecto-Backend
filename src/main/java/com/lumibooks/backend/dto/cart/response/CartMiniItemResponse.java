package com.lumibooks.backend.dto.cart.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información de un ítem en el carrito de compras 
 * para la vista mini del carrito (CartMiniResponse).
 */
@Getter
@Builder
public class CartMiniItemResponse {

    private Long cartItemId;
    private Long bookId;
    private String coverImageUrl;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;

}