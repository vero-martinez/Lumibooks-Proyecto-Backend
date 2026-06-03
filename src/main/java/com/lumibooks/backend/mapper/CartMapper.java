package com.lumibooks.backend.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.cart.response.CartBookStatusResponse;
import com.lumibooks.backend.dto.cart.response.CartItemResponse;
import com.lumibooks.backend.dto.cart.response.CartMiniItemResponse;
import com.lumibooks.backend.dto.cart.response.CartMiniResponse;
import com.lumibooks.backend.dto.cart.response.CartResponse;
import com.lumibooks.backend.entity.Cart;
import com.lumibooks.backend.entity.CartItem;

/**
 * Mapper encargado de transformar entidades Cart y CartItem en DTOs de respuesta.
 */
@Component
public class CartMapper {

    // ============ Entity --> Response DTO ============

    public CartItemResponse toItemResponse(CartItem item) {
        BigDecimal unitPrice = item.getBook().getPrice();
        BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .bookId(item.getBook().getId())
                .coverImageUrl(item.getBook().getCoverImageUrl())
                .title(item.getBook().getTitle())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .subtotal(itemSubtotal)
                .build();
    }

    public CartMiniItemResponse toMiniItemResponse(CartItem item) {
        return CartMiniItemResponse.builder()
                .cartItemId(item.getId())
                .bookId(item.getBook().getId())
                .coverImageUrl(item.getBook().getCoverImageUrl())
                .title(item.getBook().getTitle())
                .quantity(item.getQuantity())
                .unitPrice(item.getBook().getPrice())
                .build();
    }

    public CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal subtotal = calculateTotal(cart);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalItems(calculateTotalItems(cart))
                .subtotal(subtotal)
                .total(subtotal)
                .build();
    }

    public CartMiniResponse toMiniResponse(Cart cart) {
        List<CartMiniItemResponse> items = cart.getItems().stream()
                .map(this::toMiniItemResponse)
                .toList();

        return CartMiniResponse.builder()
                .totalItems(calculateTotalItems(cart))
                .total(calculateTotal(cart))
                .items(items)
                .build();
    }

    public CartBookStatusResponse toBookStatusResponse(Long bookId, boolean inCart) {
        return CartBookStatusResponse.builder()
                .bookId(bookId)
                .inCart(inCart)
                .build();
    }

    // ============ Helpers privados ============

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer calculateTotalItems(Cart cart) {
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

}