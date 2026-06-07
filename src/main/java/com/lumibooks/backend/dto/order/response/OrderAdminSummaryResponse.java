package com.lumibooks.backend.dto.order.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.OrderStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar un resumen de las órdenes en el panel de administración.
 */
@Getter
@Builder
public class OrderAdminSummaryResponse {

    private Long id;
    private String orderNumber;
    private String clientName;
    private String dni;
    private OrderStatus status;
    private String managerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}