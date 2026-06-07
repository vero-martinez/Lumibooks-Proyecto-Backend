package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.order.request.OrderCreateRequest;
import com.lumibooks.backend.dto.order.response.CheckoutPreviewResponse;
import com.lumibooks.backend.dto.order.response.OrderClientDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderClientResponse;
import com.lumibooks.backend.enums.OrderStatus;
import com.lumibooks.backend.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/client/orders")
@RequiredArgsConstructor
public class OrderClientController {

    private final OrderService orderService;

    @GetMapping("/preview")
    public ResponseEntity<CheckoutPreviewResponse> getCheckoutPreview(
            @RequestParam Long addressId) {
        return ResponseEntity.ok(orderService.getCheckoutPreview(addressId));
    }

    @PostMapping
    public ResponseEntity<OrderClientResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderClientResponse>> getMyOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getMyOrders(status, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderClientDetailResponse> getMyOrderDetail(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getMyOrderDetail(orderId));
    }

}