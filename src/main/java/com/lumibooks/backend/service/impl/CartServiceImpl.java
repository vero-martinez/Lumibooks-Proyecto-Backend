package com.lumibooks.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.cart.request.CartAddItemRequest;
import com.lumibooks.backend.dto.cart.request.CartMergeRequest;
import com.lumibooks.backend.dto.cart.request.CartUpdateQuantityRequest;
import com.lumibooks.backend.dto.cart.response.CartBookStatusResponse;
import com.lumibooks.backend.dto.cart.response.CartMiniResponse;
import com.lumibooks.backend.dto.cart.response.CartResponse;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Cart;
import com.lumibooks.backend.entity.CartItem;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.CartMapper;
import com.lumibooks.backend.repository.BookRepository;
import com.lumibooks.backend.repository.CartItemRepository;
import com.lumibooks.backend.repository.CartRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.CartService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión del carrito de compras.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // ============ Consultas ============

    @Override
    @Transactional
    public CartResponse getCart() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartMiniResponse getMiniCart() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);
        return cartMapper.toMiniResponse(cart);
    }

    @Override
    @Transactional
    public CartBookStatusResponse getBookStatus(Long bookId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);
        boolean inCart = cartItemRepository.existsByCartIdAndBookId(cart.getId(), bookId);
        return cartMapper.toBookStatusResponse(bookId, inCart);
    }

    // ============ Operaciones ============

    @Override
    @Transactional
    public void addItem(CartAddItemRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);

        Book book = bookRepository.findByIdAndIsActiveTrue(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + request.getBookId()));

        var existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), request.getBookId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            validateStock(book, newQuantity);
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
            return;
        }

        validateStock(book, request.getQuantity());

        CartItem item = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeItem(Long bookId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);

        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El libro no está en el carrito"));

        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void updateQuantity(Long bookId, CartUpdateQuantityRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);

        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El libro no está en el carrito"));

        validateStock(item.getBook(), request.getQuantity());

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    @Override
    @Transactional
    public void mergeCart(CartMergeRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = resolveCart(user);

        for (CartAddItemRequest mergeItem : request.getItems()) {
            Book book = bookRepository.findByIdAndIsActiveTrue(mergeItem.getBookId())
                    .orElse(null);

            if (book == null) continue;

            cartItemRepository.findByCartIdAndBookId(cart.getId(), mergeItem.getBookId())
                    .ifPresentOrElse(
                            existingItem -> {
                                int newQuantity = existingItem.getQuantity() + mergeItem.getQuantity();
                                int finalQuantity = Math.min(newQuantity, book.getStock());
                                existingItem.setQuantity(finalQuantity);
                                cartItemRepository.save(existingItem);
                            },
                            () -> {
                                if (book.getStock() < 1) return;
                                int finalQuantity = Math.min(mergeItem.getQuantity(), book.getStock());
                                CartItem item = CartItem.builder()
                                        .cart(cart)
                                        .book(book)
                                        .quantity(finalQuantity)
                                        .build();
                                cartItemRepository.save(item);
                            }
                    );
        }
    }

    // ============ Helpers privados ============
    private Cart resolveCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .build()));
    }

    private void validateStock(Book book, Integer quantity) {
        if (book.getStock() < quantity) {
            throw new BadRequestException(
                    "Stock insuficiente para el libro: " + book.getTitle() +
                    ". Stock disponible: " + book.getStock());
        }
    }

}