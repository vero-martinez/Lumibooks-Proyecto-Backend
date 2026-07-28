package com.lumibooks.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.book.request.BookCreateRequest;
import com.lumibooks.backend.dto.book.request.BookUpdateRequest;
import com.lumibooks.backend.dto.book.response.BookAdminDetailResponse;
import com.lumibooks.backend.dto.book.response.BookCardResponse;
import com.lumibooks.backend.dto.book.response.BookDetailResponse;
import com.lumibooks.backend.dto.book.response.BookSuggestionResponse;
import com.lumibooks.backend.dto.book.response.BookSummaryResponse;
import com.lumibooks.backend.dto.cloudinary.ImageUploadResponse;
import com.lumibooks.backend.dto.book.response.BookResponse;
import com.lumibooks.backend.entity.Author;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Category;
import com.lumibooks.backend.entity.Publisher;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.enums.ReviewStatus;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.BookMapper;
import com.lumibooks.backend.repository.AuthorRepository;
import com.lumibooks.backend.repository.BookRepository;
import com.lumibooks.backend.repository.CategoryRepository;
import com.lumibooks.backend.repository.PublisherRepository;
import com.lumibooks.backend.repository.ReviewRepository;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.BookService;
import com.lumibooks.backend.service.CloudinaryService;
import com.lumibooks.backend.service.NotificationService;
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
    private final ReviewRepository reviewRepository;
    private final BookMapper bookMapper;
    private final UserRepository userRepository;

    private final NotificationService notificationService;
    private final ActionLogService actionLogService;
    private final CloudinaryService cloudinaryService;

    // ===================== Catálogo público =====================

    // Obtiene los 10 libros más recientes
    @Override
    public List<BookCardResponse> getLatestBooks() {
        List<Book> books = bookRepository.findTop10ByIsActiveTrueOrderByCreatedAtDesc();
        Map<Long, double[]> statsMap = getRatingStatsMap(books.stream().map(Book::getId).toList());
        return books.stream()
                .map(book -> toCardResponseWithStats(book, statsMap))
                .toList();
    }

    // Obtiene los 10 libros mejor valorados
    @Override
    public List<BookCardResponse> getTopRatedBooks() {
        List<Long> bookIds = reviewRepository.findTop10BookIdsByAverageRating(ReviewStatus.OCULTA);
        List<Book> books = bookRepository.findAllById(bookIds);
        Map<Long, double[]> statsMap = getRatingStatsMap(bookIds);
        return books.stream()
                .map(book -> toCardResponseWithStats(book, statsMap))
                .toList();
    }

    // Obtiene la lista de todos los libros aplicando filtros
    @Override
    public Page<BookCardResponse> getBooks(
            String search,
            Long categoryId,
            Long publisherId,
            Long authorId,
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

        if (authorId != null) {
            spec = spec.and(BookSpecification.hasAuthor(authorId));
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

        

        Page<Book> books = bookRepository.findAll(spec, pageable);
        Map<Long, double[]> statsMap = getRatingStatsMap(books.map(Book::getId).toList());
        return books.map(book -> toCardResponseWithStats(book, statsMap));
    }

    // Devuelve sugerencias de búsqueda
    @Override
    public List<BookSuggestionResponse> getBookSuggestions(String search) {

        if (search == null || search.trim().length() < 2) {
            return List.of();
        }

        Specification<Book> spec = BookSpecification.isActive()
                .and(BookSpecification.search(search));

        Pageable pageable = PageRequest.of(0, 5);

        return bookRepository.findAll(spec, pageable)
                .stream()
                .map(bookMapper::toSuggestionResponse)
                .toList();
    }

    // Obtiene el detalle completo de un libro
    @Override
    public BookDetailResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
        double[] stats = getStats(book.getId(), getRatingStatsMap(List.of(book.getId())));
        return bookMapper.toDetailResponse(book, stats[0], (long) stats[1]);
    }

    // ===================== Administración =======================

    // Obtiene lista de libros para el panel de admin
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

    // Obtener el detalle completo de un libro
    @Override
    public BookAdminDetailResponse getBookDetailAdmin(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));
        double[] stats = getStats(book.getId(), getRatingStatsMap(List.of(book.getId())));
        return bookMapper.toAdminDetailResponse(book, stats[0], (long) stats[1]);
    }

    // ===================== Gestión de libros ====================

    // Registrar un nuevo libro
    @Override
    @Transactional
    public BookResponse createBook(BookCreateRequest request, MultipartFile coverImage) {
        validateIsbnNotExists(request.getIsbn());

        ImageUploadResponse imageResponse = cloudinaryService.uploadImage(coverImage, "lumibooks/books");

        Book book = bookMapper.toEntity(request);
        book.setCoverImageUrl(imageResponse.getSecureUrl());
        book.setCoverImagePublicId(imageResponse.getPublicId());
        book.setPublisher(getPublisherOrThrow(request.getPublisherId()));
        book.setAuthors(getAuthorsOrThrow(request.getAuthorIds()));
        book.setCategories(getCategoriesOrThrow(request.getCategoryIds()));

        Book saved = bookRepository.save(book);

        actionLogService.log(
                ActionType.CREAR,
                EntityType.BOOK,
                saved.getId(),
                "Creó el libro '" + saved.getTitle() + "'");
        return bookMapper.toBookResponse(saved);
    }

    // Actualizar un libro existente
    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookUpdateRequest request, MultipartFile coverImage) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));

        Integer previousStock = book.getStock();

        if (coverImage != null && !coverImage.isEmpty()) {
            ImageUploadResponse imageResponse = cloudinaryService.replaceImage(
                    book.getCoverImagePublicId(), coverImage, "lumibooks/books");
            book.setCoverImageUrl(imageResponse.getSecureUrl());
            book.setCoverImagePublicId(imageResponse.getPublicId());
        }

        bookMapper.updateEntity(request, book);

        if (request.getPublisherId() != null) {
            book.setPublisher(getPublisherOrThrow(request.getPublisherId()));
        }
        if (request.getAuthorIds() != null) {
            book.setAuthors(getAuthorsOrThrow(request.getAuthorIds()));
        }
        if (request.getCategoryIds() != null) {
            book.setCategories(getCategoriesOrThrow(request.getCategoryIds()));
        }

        Book savedBook = bookRepository.save(book);

        if (request.getStock() != null) {
            notifyStockChanges(savedBook, previousStock, request.getStock());
        }
        actionLogService.log(
                ActionType.EDITAR,
                EntityType.BOOK,
                savedBook.getId(),
                "Editó el libro '" + savedBook.getTitle() + "'");
        return bookMapper.toBookResponse(savedBook);
    }

    // Activar o desactivar un libro
    @Override
    @Transactional
    public void toggleBookStatus(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));
        book.setActive(!book.isActive());
        bookRepository.save(book);

        actionLogService.log(
                ActionType.CAMBIAR_ESTADO,
                EntityType.BOOK,
                book.getId(),
                "Cambió el estado del libro '" + book.getTitle() + "' a "
                        + (book.isActive() ? "ACTIVO" : "INACTIVO"));
    }

    // ===================== Helpers privados =====================

    // Valida que el ISBN no esté registrado.
    private void validateIsbnNotExists(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BadRequestException("Ya existe un libro con el ISBN: " + isbn);
        }
    }

    // Obtiene una editorial por su ID o lanza una excepción si no existe.
    private Publisher getPublisherOrThrow(Long publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editorial no encontrada con id: " + publisherId));
    }

    // Obtiene los autores por sus IDs o lanza una excepción si alguno no existe.
    private Set<Author> getAuthorsOrThrow(Set<Long> authorIds) {
        Set<Author> authors = Set.copyOf(authorRepository.findAllById(authorIds));
        if (authors.size() != authorIds.size()) {
            throw new ResourceNotFoundException("Uno o más autores no fueron encontrados");
        }
        return authors;
    }

    // Obtiene las categorías por sus IDs o lanza una excepción si alguna no existe.
    private Set<Category> getCategoriesOrThrow(Set<Long> categoryIds) {
        Set<Category> categories = Set.copyOf(categoryRepository.findAllById(categoryIds));
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("Una o más categorías no fueron encontradas");
        }
        return categories;
    }

    // Obtiene las estadísticas de calificación de varios libros.
    private Map<Long, double[]> getRatingStatsMap(List<Long> bookIds) {
        return reviewRepository
                .findRatingStatsByBookIds(bookIds, ReviewStatus.OCULTA)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new double[] { (Double) row[1], (double) (Long) row[2] }));
    }

    // Obtiene las estadísticas de calificación de un libro.
    private double[] getStats(Long bookId, Map<Long, double[]> statsMap) {
        return statsMap.getOrDefault(bookId, new double[] { 0.0, 0 });
    }

    // Convierte un libro a una tarjeta con estadísticas
    private BookCardResponse toCardResponseWithStats(Book book, Map<Long, double[]> statsMap) {
        double[] stats = getStats(book.getId(), statsMap);
        return bookMapper.toCardResponse(book, stats[0], (long) stats[1]);
    }

    // Envía notificaciones por cambios de stock
    private void notifyStockChanges(Book book, Integer previousStock, Integer newStock) {

        // Stock agotado → notificar al admin
        if (newStock == 0) {
            userRepository.findByRoleAndIsActiveTrue(RoleUser.ADMIN)
                    .forEach(admin -> notificationService.sendNotification(
                            admin,
                            "Stock agotado",
                            "El libro \"" + book.getTitle() + "\" se ha quedado sin stock."));
        }

    }

}