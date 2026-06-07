package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumibooks.backend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Obtener items de una orden
    List<OrderItem> findByOrderId(Long orderId);

}