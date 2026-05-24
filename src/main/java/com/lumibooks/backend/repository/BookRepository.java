package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // Verificar si ya existe un libro con ese ISBN
    boolean existsByIsbn(String isbn);

    // Obtener los 10 libros activos más recientes
    List<Book> findTop10ByIsActiveTrueOrderByCreatedAtDesc();

    // Contar libros con stock bajo
    long countByStockLessThanEqual(int threshold);

    // Contar libros inactivos
    long countByIsActiveFalse();

    // Obtener libro si esta activo
    Optional<Book> findByIdAndIsActiveTrue(Long id);

}