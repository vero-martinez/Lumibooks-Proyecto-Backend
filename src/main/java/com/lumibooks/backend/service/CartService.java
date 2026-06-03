package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.cart.request.CartAddItemRequest;
import com.lumibooks.backend.dto.cart.request.CartMergeRequest;
import com.lumibooks.backend.dto.cart.request.CartUpdateQuantityRequest;
import com.lumibooks.backend.dto.cart.response.CartBookStatusResponse;
import com.lumibooks.backend.dto.cart.response.CartMiniResponse;
import com.lumibooks.backend.dto.cart.response.CartResponse;

/**
 * Interfaz para la gestión del carrito de compras del usuario autenticado.
 */
public interface CartService {

    /**
     * Retorna el carrito completo del usuario para la página de carrito.
     * Si no existe, lo crea automáticamente.
     * @return carrito completo con items y totales
     */
    CartResponse getCart();

    /**
     * Retorna el mini carrito para el navbar.
     * Si no existe, lo crea automáticamente.
     * @return mini carrito con items resumidos y total
     */
    CartMiniResponse getMiniCart();

    /**
     * Agrega un libro al carrito.
     * Si no existe carrito, lo crea automáticamente.
     * @param request contiene bookId y quantity
     * @throws ResourceNotFoundException si el libro no existe
     * @throws BadRequestException si el libro ya está en el carrito o no hay stock suficiente
     */
    void addItem(CartAddItemRequest request);

    /**
     * Quita un libro del carrito.
     * @param bookId identificador del libro
     * @throws ResourceNotFoundException si el libro no está en el carrito
     */
    void removeItem(Long bookId);

    /**
     * Actualiza la cantidad de un libro en el carrito.
     * @param bookId identificador del libro
     * @param request nueva cantidad
     * @throws ResourceNotFoundException si el libro no está en el carrito
     * @throws BadRequestException si no hay stock suficiente
     */
    void updateQuantity(Long bookId, CartUpdateQuantityRequest request);

    /**
     * Vacía el carrito eliminando todos los items.
     */
    void clearCart();

    /**
     * Verifica si un libro está en el carrito del usuario.
     * @param bookId identificador del libro
     * @return estado del libro en el carrito
     */
    CartBookStatusResponse getBookStatus(Long bookId);

    /**
     * Fusiona el carrito anónimo con el carrito del usuario autenticado.
     * Si un libro ya está en el carrito suma las cantidades respetando el stock.
     * @param request lista de items del carrito anónimo
     */
    void mergeCart(CartMergeRequest request);

}