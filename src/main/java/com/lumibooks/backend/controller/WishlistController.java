package com.lumibooks.backend.controller;

import java.util.List;

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

import com.lumibooks.backend.dto.wishlist.request.WishlistMoveBookRequest;
import com.lumibooks.backend.dto.wishlist.request.WishlistNameRequest;
import com.lumibooks.backend.dto.wishlist.response.WishlistBookStatusResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistDetailResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistResponse;
import com.lumibooks.backend.service.WishlistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de listas de deseos del usuario autenticado.
 */
@RestController
@RequestMapping("/api/client/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Retorna todas las listas del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlists() {
        return ResponseEntity.ok(wishlistService.getWishlists());
    }

    /**
     * Retorna el detalle de una lista con sus libros.
     */
    @GetMapping("/{wishlistId}")
    public ResponseEntity<WishlistDetailResponse> getWishlistDetail(@PathVariable Long wishlistId) {
        return ResponseEntity.ok(wishlistService.getWishlistDetail(wishlistId));
    }

    /**
     * Crea una nueva lista de deseos.
     */
    @PostMapping
    public ResponseEntity<WishlistResponse> createWishlist(
            @RequestBody @Valid WishlistNameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.createWishlist(request));
    }

    /**
     * Renombra una lista de deseos.
     */
    @PatchMapping("/{wishlistId}")
    public ResponseEntity<WishlistResponse> renameWishlist(
            @PathVariable Long wishlistId,
            @RequestBody @Valid WishlistNameRequest request) {
        return ResponseEntity.ok(wishlistService.renameWishlist(wishlistId, request));
    }

    /**
     * Elimina una lista de deseos.
     */
    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long wishlistId) {
        wishlistService.deleteWishlist(wishlistId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Agrega un libro a una lista de deseos.
     */
    @PostMapping("/{wishlistId}/books/{bookId}")
    public ResponseEntity<Void> addBook(
            @PathVariable Long wishlistId,
            @PathVariable Long bookId) {
        wishlistService.addBook(wishlistId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Quita un libro de una lista de deseos.
     */
    @DeleteMapping("/{wishlistId}/books/{bookId}")
    public ResponseEntity<Void> removeBook(
            @PathVariable Long wishlistId,
            @PathVariable Long bookId) {
        wishlistService.removeBook(wishlistId, bookId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mueve un libro de una lista a otra.
     */
    @PatchMapping("/{wishlistId}/books/{bookId}/move")
    public ResponseEntity<Void> moveBook(
            @PathVariable Long wishlistId,
            @PathVariable Long bookId,
            @RequestBody @Valid WishlistMoveBookRequest request) {
        wishlistService.moveBook(wishlistId, bookId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica en qué listas del usuario está un libro.
     */
    @GetMapping("/books/{bookId}/status")
    public ResponseEntity<WishlistBookStatusResponse> getBookStatus(@PathVariable Long bookId) {
        return ResponseEntity.ok(wishlistService.getBookStatus(bookId));
    }

}