package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.wishlist.request.WishlistMoveBookRequest;
import com.lumibooks.backend.dto.wishlist.request.WishlistNameRequest;
import com.lumibooks.backend.dto.wishlist.response.WishlistBookStatusResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistDetailResponse;
import com.lumibooks.backend.dto.wishlist.response.WishlistResponse;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.entity.Wishlist;
import com.lumibooks.backend.entity.WishlistItem;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.WishlistMapper;
import com.lumibooks.backend.repository.BookRepository;
import com.lumibooks.backend.repository.WishlistItemRepository;
import com.lumibooks.backend.repository.WishlistRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.WishlistService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de listas de deseos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // ============ Listas ============

    @Override
    public List<WishlistResponse> getWishlists() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return wishlistRepository.findByUserId(user.getId())
                .stream()
                .map(wishlistMapper::toResponse)
                .toList();
    }

    @Override
    public WishlistDetailResponse getWishlistDetail(Long wishlistId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Wishlist wishlist = resolveWishlist(wishlistId, user.getId());
        return wishlistMapper.toWishlistDetailResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse createWishlist(WishlistNameRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        // Límite máximo de listas es 5 por usuario
        long totalWishlists = wishlistRepository.countByUserId(user.getId());
        if (totalWishlists >= 5) {
            throw new BadRequestException("No puedes tener más de 5 listas de deseos");
        }

        validateNameNotTaken(user.getId(), request.getName(), null);

        Wishlist wishlist = wishlistMapper.toEntity(request, user);
        return wishlistMapper.toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public WishlistResponse renameWishlist(Long wishlistId, WishlistNameRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Wishlist wishlist = resolveWishlist(wishlistId, user.getId());
        validateNameNotTaken(user.getId(), request.getName(), wishlistId);

        wishlistMapper.renameEntity(request, wishlist);
        return wishlistMapper.toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public void deleteWishlist(Long wishlistId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Wishlist wishlist = resolveWishlist(wishlistId, user.getId());
        wishlistRepository.delete(wishlist);
    }

    // ============ Items ============

    @Override
    @Transactional
    public void addBook(Long wishlistId, Long bookId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Wishlist wishlist = resolveWishlist(wishlistId, user.getId());

        if (wishlistItemRepository.existsByWishlistIdAndBookId(wishlistId, bookId)) {
            throw new BadRequestException("El libro ya está en esta lista");
        }

        Book book = bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + bookId));

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .book(book)
                .build();

        wishlistItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeBook(Long wishlistId, Long bookId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        resolveWishlist(wishlistId, user.getId());

        WishlistItem item = wishlistItemRepository.findByWishlistIdAndBookId(wishlistId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El libro no está en esta lista"));

        wishlistItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void moveBook(Long wishlistId, Long bookId, WishlistMoveBookRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        resolveWishlist(wishlistId, user.getId());

        Wishlist targetWishlist = resolveWishlist(request.getTargetWishlistId(), user.getId());

        if (wishlistItemRepository.existsByWishlistIdAndBookId(request.getTargetWishlistId(), bookId)) {
            throw new BadRequestException("El libro ya está en la lista destino");
        }

        WishlistItem item = wishlistItemRepository.findByWishlistIdAndBookId(wishlistId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El libro no está en esta lista"));

        item.setWishlist(targetWishlist);
        wishlistItemRepository.save(item);
    }

    @Override
    public WishlistBookStatusResponse getBookStatus(Long bookId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        List<Wishlist> wishlists = wishlistRepository.findByUserIdAndBookId(user.getId(), bookId);
        return wishlistMapper.toBookStatusResponse(bookId, wishlists);
    }

    // ============ Helpers privados ============

    private Wishlist resolveWishlist(Long wishlistId, Long userId) {
        return wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lista no encontrada con id: " + wishlistId));
    }

    private void validateNameNotTaken(Long userId, String name, Long excludeId) {
        boolean taken = excludeId == null
                ? wishlistRepository.existsByUserIdAndNameIgnoreCase(userId, name)
                : wishlistRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, excludeId);

        if (taken) {
            throw new BadRequestException("Ya tienes una lista con el nombre: " + name);
        }
    }

}