package com.lumibooks.backend.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.order.response.CheckoutAddressResponse;
import com.lumibooks.backend.dto.order.response.CheckoutPreviewResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminSummaryResponse;
import com.lumibooks.backend.dto.order.response.OrderClientDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderClientResponse;
import com.lumibooks.backend.dto.order.response.OrderItemResponse;
import com.lumibooks.backend.entity.Address;
import com.lumibooks.backend.entity.Cart;
import com.lumibooks.backend.entity.CartItem;
import com.lumibooks.backend.entity.Order;
import com.lumibooks.backend.entity.OrderItem;

import lombok.RequiredArgsConstructor;

/**
 * Mapper encargado de transformar entidades Order y OrderItem en DTOs de
 * respuesta.
 */
@RequiredArgsConstructor
@Component
public class OrderMapper {

        // ============ Entity --> Response DTO ============

        public OrderClientResponse toClientResponse(Order order) {
                return OrderClientResponse.builder()
                                .id(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .total(order.getTotal())
                                .status(order.getStatus())
                                .createdAt(order.getCreatedAt().toLocalDate())
                                .build();
        }

        public OrderClientDetailResponse toClientDetailResponse(Order order) {
                return OrderClientDetailResponse.builder()
                                .id(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .status(order.getStatus())
                                .recipientName(order.getRecipientName())
                                .dni(order.getDni())
                                .phone(order.getPhone())
                                .addressLine(order.getAddress().getAddressLine())
                                .districtName(order.getAddress().getDistrict().getName())
                                .provinceName(order.getAddress().getDistrict().getProvince().getName())
                                .departmentName(order.getAddress().getDistrict().getProvince().getDepartment()
                                                .getName())
                                .subtotal(order.getSubtotal())
                                .shippingCost(order.getShippingCost())
                                .total(order.getTotal())
                                .items(extractOrderItems(order))
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .build();
        }

        public OrderAdminSummaryResponse toAdminSummaryResponse(Order order) {
                return OrderAdminSummaryResponse.builder()
                                .id(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .clientName(order.getUser().getFullName())
                                .dni(order.getUser().getDni())
                                .status(order.getStatus())
                                .managerName(order.getAssignedManager() != null
                                                ? order.getAssignedManager().getFullName()
                                                : null)
                                .createdAt(order.getCreatedAt())
                                .build();
        }

        public OrderAdminDetailResponse toAdminDetailResponse(Order order) {
                return OrderAdminDetailResponse.builder()
                                .id(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .clientName(order.getUser().getFullName())
                                .managerName(order.getAssignedManager() != null
                                                ? order.getAssignedManager().getFullName()
                                                : null)
                                .status(order.getStatus())
                                .recipientName(order.getRecipientName())
                                .dni(order.getDni())
                                .phone(order.getPhone())
                                .addressLine(order.getAddress().getAddressLine())
                                .districtName(order.getAddress().getDistrict().getName())
                                .provinceName(order.getAddress().getDistrict().getProvince().getName())
                                .departmentName(order.getAddress().getDistrict().getProvince().getDepartment()
                                                .getName())
                                .subtotal(order.getSubtotal())
                                .shippingCost(order.getShippingCost())
                                .total(order.getTotal())
                                .items(extractOrderItems(order))
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .build();
        }

        public CheckoutAddressResponse toCheckoutAddressResponse(Address address) {
                return CheckoutAddressResponse.builder()
                                .id(address.getId())
                                .addressLine(address.getAddressLine())
                                .reference(address.getReference())
                                .districtName(address.getDistrict().getName())
                                .provinceName(address.getDistrict().getProvince().getName())
                                .departmentName(address.getDistrict().getProvince().getDepartment().getName())
                                .shippingCost(address.getDistrict().getShippingCost())
                                .isShippingAvailable(address.getDistrict().isShippingAvailable())
                                .build();
        }

        public CheckoutPreviewResponse toCheckoutPreviewResponse(Cart cart, Address address) {
                List<OrderItemResponse> items = cart.getItems().stream()
                                .map(this::toOrderItemResponseFromCart)
                                .toList();

                BigDecimal subtotal = items.stream()
                                .map(OrderItemResponse::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal shippingCost = address.getDistrict().getShippingCost();

                return CheckoutPreviewResponse.builder()
                                .items(items)
                                .address(toCheckoutAddressResponse(address))
                                .subtotal(subtotal)
                                .total(subtotal.add(shippingCost))
                                .build();
        }

        public OrderItemResponse toOrderItemResponse(OrderItem item) {
                return OrderItemResponse.builder()
                                .bookId(item.getBook().getId())
                                .coverImageUrl(item.getCoverImageUrl())
                                .title(item.getTitle())
                                .unitPrice(item.getUnitPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build();
        }

        // ============ Helpers privados ============

        private List<OrderItemResponse> extractOrderItems(Order order) {
                return order.getItems().stream()
                                .map(this::toOrderItemResponse)
                                .toList();
        }

        private OrderItemResponse toOrderItemResponseFromCart(CartItem cartItem) {
                BigDecimal unitPrice = cartItem.getBook().getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                return OrderItemResponse.builder()
                                .bookId(cartItem.getBook().getId())
                                .coverImageUrl(cartItem.getBook().getCoverImageUrl())
                                .title(cartItem.getBook().getTitle())
                                .unitPrice(unitPrice)
                                .quantity(cartItem.getQuantity())
                                .subtotal(subtotal)
                                .build();
        }

}