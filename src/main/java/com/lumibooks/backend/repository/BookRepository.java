package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // Verificar si ya existe un libro con ese ISBN
    boolean existsByIsbn(String isbn);

    // Los 10 libros activos más recientes con autores, categorías y editorial precargados (evita N+1)
    @EntityGraph(attributePaths = { "authors", "categories", "publisher" })
    List<Book> findTop10ByIsActiveTrueOrderByCreatedAtDesc();

    // Contar libros con stock igual o menor al umbral indicado
    long countByStockLessThanEqual(int threshold);

    // Contar libros inactivos
    long countByIsActiveFalse();

    // Obtener un libro activo por ID con autores, categorías y editorial precargados (evita N+1)
    @EntityGraph(attributePaths = { "authors", "categories", "publisher" })
    Optional<Book> findByIdAndIsActiveTrue(Long id);

    // Obtener un libro por ID con autores, categorías y editorial precargados (evita N+1) — usado en admin
    @EntityGraph(attributePaths = { "authors", "categories", "publisher" })
    Optional<Book> findById(Long id);

    // Obtener libros por IDs con autores, categorías y editorial precargados (evita N+1) — usado en top rated
    @EntityGraph(attributePaths = { "authors", "categories", "publisher" })
    List<Book> findAllById(Iterable<Long> ids);

    // Listado paginado con filtros dinámicos; carga autores, categorías y editorial (evita N+1)
    @EntityGraph(attributePaths = { "authors", "categories", "publisher" })
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

}