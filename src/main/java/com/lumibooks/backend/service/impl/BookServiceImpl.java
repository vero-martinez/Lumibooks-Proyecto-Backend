package com.lumibooks.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.BookCreateRequest;
import com.lumibooks.backend.dto.request.BookUpdateRequest;
import com.lumibooks.backend.dto.response.BookAdminDetailResponse;
import com.lumibooks.backend.dto.response.BookCardResponse;
import com.lumibooks.backend.dto.response.BookDetailResponse;
import com.lumibooks.backend.dto.response.BookSummaryResponse;
import com.lumibooks.backend.entity.Author;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Category;
import com.lumibooks.backend.entity.Publisher;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.BookMapper;
import com.lumibooks.backend.repository.AuthorRepository;
import com.lumibooks.backend.repository.BookRepository;
import com.lumibooks.backend.repository.CategoryRepository;
import com.lumibooks.backend.repository.PublisherRepository;
import com.lumibooks.backend.service.BookService;
import com.lumibooks.backend.specification.BookSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de libros.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final BookMapper bookMapper;

    // ============ Público ============

    @Override
    public List<BookCardResponse> getLatestBooks() {
        return bookRepository.findTop10ByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    @Override
    public Page<BookCardResponse> getBooks(
            String search,
            Long categoryId,
            Long publisherId,
            BookLanguage language,
            BookFormat format,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        Specification<Book> spec = BookSpecification.isActive();

        if (search != null && !search.isBlank()) {
            spec = spec.and(BookSpecification.search(search));
        }
        if (categoryId != null) {
            spec = spec.and(BookSpecification.hasCategory(categoryId));
        }
        if (publisherId != null) {
            spec = spec.and(BookSpecification.hasPublisher(publisherId));
        }
        if (language != null) {
            spec = spec.and(BookSpecification.hasLanguage(language));
        }
        if (format != null) {
            spec = spec.and(BookSpecification.hasFormat(format));
        }
        if (minPrice != null) {
            spec = spec.and(BookSpecification.hasMinPrice(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(BookSpecification.hasMaxPrice(maxPrice));
        }

        return bookRepository.findAll(spec, pageable)
                .map(bookMapper::toCardResponse);
    }

    @Override
    public BookDetailResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado"));
        return bookMapper.toDetailResponse(book);
    }

    // ============ Admin ============

    @Override
    public Page<BookSummaryResponse> getBooksAdmin(
            String search,
            Boolean isActive,
            BookLanguage language,
            Pageable pageable) {

        Specification<Book> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(BookSpecification.search(search));
        }
        if (isActive != null) {
            spec = spec.and(BookSpecification.hasActive(isActive));
        }
        if (language != null) {
            spec = spec.and(BookSpecification.hasLanguage(language));
        }

        return bookRepository.findAll(spec, pageable)
                .map(bookMapper::toSummaryResponse);
    }

    @Override
    public BookAdminDetailResponse getBookDetailAdmin(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));
        return bookMapper.toAdminDetailResponse(book);
    }

    @Override
    @Transactional
    public BookAdminDetailResponse createBook(BookCreateRequest request) {
        validateIsbnNotExists(request.getIsbn());

        Publisher publisher = resolvePublisher(request.getPublisherId());
        Set<Author> authors = resolveAuthors(request.getAuthorIds());
        Set<Category> categories = resolveCategories(request.getCategoryIds());

        Book book = bookMapper.toEntity(request);
        book.setPublisher(publisher);
        book.setAuthors(authors);
        book.setCategories(categories);

        return bookMapper.toAdminDetailResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookAdminDetailResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));

        bookMapper.updateEntity(request, book);

        if (request.getPublisherId() != null) {
            book.setPublisher(resolvePublisher(request.getPublisherId()));
        }
        if (request.getAuthorIds() != null) {
            book.setAuthors(resolveAuthors(request.getAuthorIds()));
        }
        if (request.getCategoryIds() != null) {
            book.setCategories(resolveCategories(request.getCategoryIds()));
        }

        return bookMapper.toAdminDetailResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void toggleBookStatus(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));
        book.setActive(!book.isActive());
        bookRepository.save(book);
    }

    // ============ Helpers privados ============

    private void validateIsbnNotExists(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BadRequestException("Ya existe un libro con el ISBN: " + isbn);
        }
    }

    private Publisher resolvePublisher(Long publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editorial no encontrada con id: " + publisherId));
    }

    private Set<Author> resolveAuthors(Set<Long> authorIds) {
        Set<Author> authors = Set.copyOf(authorRepository.findAllById(authorIds));
        if (authors.size() != authorIds.size()) {
            throw new ResourceNotFoundException("Uno o más autores no fueron encontrados");
        }
        return authors;
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = Set.copyOf(categoryRepository.findAllById(categoryIds));
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("Una o más categorías no fueron encontradas");
        }
        return categories;
    }

}