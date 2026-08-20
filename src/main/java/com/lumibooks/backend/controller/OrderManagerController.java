package com.lumibooks.backend.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.order.request.OrderStatusUpdateRequest;
import com.lumibooks.backend.dto.order.response.OrderAdminDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminSummaryResponse;
import com.lumibooks.backend.enums.OrderStatus;
import com.lumibooks.backend.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager/orders")
@RequiredArgsConstructor
public class OrderManagerController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderAdminSummaryResponse>> getManagerOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getManagerOrders(
                search, status, dateFrom, dateTo, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderAdminDetailResponse> getManagerOrderDetail(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getManagerOrderDetail(orderId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderStatusUpdateRequest request) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.noContent().build();
    }

}