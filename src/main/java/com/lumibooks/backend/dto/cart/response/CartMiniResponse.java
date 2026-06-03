package com.lumibooks.backend.dto.cart.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información del carrito 
 * de compras en su vista mini (dropdown).
 */
@Getter
@Builder
public class CartMiniResponse {

    private Integer totalItems;
    private BigDecimal total;
    private List<CartMiniItemResponse> items;

}