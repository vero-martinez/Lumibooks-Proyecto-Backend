package com.lumibooks.backend.dto.cart.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información completa del carrito de compras, 
 * incluyendo los ítems, totales y subtotales.
 */
@Getter
@Builder
public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items;
    private Integer totalItems;
    private BigDecimal subtotal;
    private BigDecimal total;

}