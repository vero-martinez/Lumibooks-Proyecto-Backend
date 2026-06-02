package com.lumibooks.backend.service;

import java.util.List;

import com.lumibooks.backend.dto.wishlist.request.WishlistMoveBookRequest;
import com.lumibooks.backend.dto.wishlist.request.WishlistNameRequest;
import com.lumibooks.backend.dto.wishlist.response.WishlistBookStatusResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistDetailResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistResponse;

/**
 * Interfaz para la gestión de listas de deseos del usuario autenticado.
 */
public interface WishlistService {

    /**
     * Retorna todas las listas del usuario autenticado.
     * @return lista de wishlists con nombre y cantidad de libros
     */
    List<WishlistResponse> getWishlists();

    /**
     * Retorna el detalle de una lista con sus libros.
     * @param wishlistId identificador de la lista
     * @return detalle de la lista con libros en formato card
     * @throws ResourceNotFoundException si la lista no existe o no pertenece al usuario
     */
    WishlistDetailResponse getWishlistDetail(Long wishlistId);

    /**
     * Crea una nueva lista de deseos.
     * @param request nombre de la lista
     * @return lista creada
     * @throws BadRequestException si ya existe una lista con ese nombre
     */
    WishlistResponse createWishlist(WishlistNameRequest request);

    /**
     * Renombra una lista de deseos.
     * @param wishlistId identificador de la lista
     * @param request nuevo nombre de la lista
     * @return lista renombrada
     * @throws ResourceNotFoundException si la lista no existe o no pertenece al usuario
     * @throws BadRequestException si ya existe una lista con ese nombre
     */
    WishlistResponse renameWishlist(Long wishlistId, WishlistNameRequest request);

    /**
     * Elimina una lista de deseos y todos sus items.
     * @param wishlistId identificador de la lista
     * @throws ResourceNotFoundException si la lista no existe o no pertenece al usuario
     */
    void deleteWishlist(Long wishlistId);

    /**
     * Agrega un libro a una lista de deseos.
     * @param wishlistId identificador de la lista
     * @param bookId identificador del libro
     * @throws ResourceNotFoundException si la lista o el libro no existen
     * @throws BadRequestException si el libro ya está en la lista
     */
    void addBook(Long wishlistId, Long bookId);

    /**
     * Quita un libro de una lista de deseos.
     * @param wishlistId identificador de la lista
     * @param bookId identificador del libro
     * @throws ResourceNotFoundException si la lista no existe o el libro no está en la lista
     */
    void removeBook(Long wishlistId, Long bookId);

    /**
     * Mueve un libro de una lista a otra.
     * @param wishlistId identificador de la lista origen
     * @param bookId identificador del libro
     * @param request identificador de la lista destino
     * @throws ResourceNotFoundException si alguna lista o el libro no existen
     * @throws BadRequestException si el libro ya está en la lista destino
     */
    void moveBook(Long wishlistId, Long bookId, WishlistMoveBookRequest request);

    /**
     * Verifica en qué listas del usuario está un libro.
     * @param bookId identificador del libro
     * @return estado del libro en las listas del usuario
     */
    WishlistBookStatusResponse getBookStatus(Long bookId);

}