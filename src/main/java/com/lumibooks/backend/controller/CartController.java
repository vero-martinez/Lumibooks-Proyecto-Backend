package com.lumibooks.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.cart.request.CartAddItemRequest;
import com.lumibooks.backend.dto.cart.request.CartMergeRequest;
import com.lumibooks.backend.dto.cart.request.CartUpdateQuantityRequest;
import com.lumibooks.backend.dto.cart.response.CartBookStatusResponse;
import com.lumibooks.backend.dto.cart.response.CartMiniResponse;
import com.lumibooks.backend.dto.cart.response.CartResponse;
import com.lumibooks.backend.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión del carrito de compras del usuario autenticado.
 */
@RestController
@RequestMapping("/api/client/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Retorna el carrito completo para la página de carrito.
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    /**
     * Retorna el mini carrito para el navbar.
     */
    @GetMapping("/mini")
    public ResponseEntity<CartMiniResponse> getMiniCart() {
        return ResponseEntity.ok(cartService.getMiniCart());
    }

    /**
     * Verifica si un libro está en el carrito.
     */
    @GetMapping("/books/{bookId}/status")
    public ResponseEntity<CartBookStatusResponse> getBookStatus(@PathVariable Long bookId) {
        return ResponseEntity.ok(cartService.getBookStatus(bookId));
    }

    /**
     * Agrega un libro al carrito.
     */
    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@RequestBody @Valid CartAddItemRequest request) {
        cartService.addItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Quita un libro del carrito.
     */
    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long bookId) {
        cartService.removeItem(bookId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza la cantidad de un libro en el carrito.
     */
    @PatchMapping("/items/{bookId}")
    public ResponseEntity<Void> updateQuantity(
            @PathVariable Long bookId,
            @RequestBody @Valid CartUpdateQuantityRequest request) {
        cartService.updateQuantity(bookId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vacía el carrito eliminando todos los items.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    /**
     * Fusiona el carrito anónimo con el carrito del usuario autenticado.
     */
    @PostMapping("/merge")
    public ResponseEntity<Void> mergeCart(@RequestBody @Valid CartMergeRequest request) {
        cartService.mergeCart(request);
        return ResponseEntity.noContent().build();
    }

}