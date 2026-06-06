package com.lumibooks.backend.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.lumibooks.backend.enums.OrderStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar un resumen de las órdenes del cliente en su perfil.
 */
@Getter
@Builder
public class OrderClientResponse {

    private Long id;
    private String orderNumber;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDate createdAt;

}