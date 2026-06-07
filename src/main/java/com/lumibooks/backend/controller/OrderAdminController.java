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

import com.lumibooks.backend.dto.order.request.OrderManagerUpdateRequest;
import com.lumibooks.backend.dto.order.response.OrderAdminDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminSummaryResponse;
import com.lumibooks.backend.enums.OrderStatus;
import com.lumibooks.backend.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderAdminSummaryResponse>> getOrdersAdmin(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersAdmin(
                orderNumber, dni, clientName, status, managerId, dateFrom, dateTo, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderAdminDetailResponse> getOrderDetailAdmin(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailAdmin(orderId));
    }

    @PatchMapping("/{orderId}/manager")
    public ResponseEntity<Void> updateOrderManager(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderManagerUpdateRequest request) {
        orderService.updateOrderManager(orderId, request);
        return ResponseEntity.noContent().build();
    }

}