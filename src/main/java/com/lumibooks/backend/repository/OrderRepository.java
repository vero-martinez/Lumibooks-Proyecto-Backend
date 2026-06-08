package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Order;
import com.lumibooks.backend.enums.OrderStatus;
import com.lumibooks.backend.enums.ReviewStatus;
import com.lumibooks.backend.enums.RoleUser;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

        // Verificar si existe una orden con ese número
        boolean existsByOrderNumber(String orderNumber);

        // Órdenes del cliente autenticado
        Page<Order> findByUserId(Long userId, Pageable pageable);

        // Órdenes del cliente filtradas por estado
        Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

        // Todas las órdenes para admin con user y manager cargados (evita N+1)
        @EntityGraph(attributePaths = { "user", "assignedManager" })
        Page<Order> findAll(Specification<Order> spec, Pageable pageable);

        // Detalle completo de una orden evitando N+1
        @Query("""
                        SELECT o FROM Order o
                        LEFT JOIN FETCH o.address a
                        LEFT JOIN FETCH a.district d
                        LEFT JOIN FETCH d.province p
                        LEFT JOIN FETCH p.department
                        LEFT JOIN FETCH o.user
                        LEFT JOIN FETCH o.assignedManager
                        LEFT JOIN FETCH o.items i
                        LEFT JOIN FETCH i.book
                        WHERE o.id = :id
                        """)
        Optional<Order> findByIdWithDetails(@Param("id") Long id);

        // Gestor con menos órdenes activas (para asignación automática)
        @Query("""
                        SELECT u.id FROM User u
                        LEFT JOIN Order o ON o.assignedManager.id = u.id
                        AND o.status IN :activeStatuses
                        WHERE u.role = :role
                        AND u.isActive = true
                        GROUP BY u.id
                        ORDER BY COUNT(o) ASC
                        LIMIT 1
                        """)
        Long findManagerIdWithLeastActiveOrders(
                        @Param("activeStatuses") List<OrderStatus> activeStatuses,
                        @Param("role") RoleUser role);

        @Query("""
                        SELECT COUNT(o) > 0 FROM Order o
                        JOIN o.items i
                        WHERE o.user.id = :userId
                        AND i.book.id = :bookId
                        AND o.status = :status
                        """)
        boolean existsDeliveredOrderWithBook(
                        @Param("userId") Long userId,
                        @Param("bookId") Long bookId,
                        @Param("status") OrderStatus status);

        @Query("""
                        SELECT DISTINCT i.book FROM Order o
                        JOIN o.items i
                        WHERE o.user.id = :userId
                        AND o.status = :status
                        AND NOT EXISTS (
                            SELECT r FROM Review r
                            WHERE r.user.id = :userId
                            AND r.book.id = i.book.id
                            AND r.status != :hiddenStatus
                        )
                        """)
        Page<Book> findBooksWithoutReviewByUserId(
                        @Param("userId") Long userId,
                        @Param("status") OrderStatus status,
                        @Param("hiddenStatus") ReviewStatus hiddenStatus,
                        Pageable pageable);
}