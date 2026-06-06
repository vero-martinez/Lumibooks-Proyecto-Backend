package com.lumibooks.backend.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumibooks.backend.enums.OrderStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa el detalle de una orden en el panel de administración.
 */
@Getter
@Builder
public class OrderAdminDetailResponse {

    private Long id;
    private String orderNumber;
    private String clientName;
    private String managerName;
    private OrderStatus status;
    private String recipientName;
    private String dni;
    private String phone;
    private String addressLine;
    private String districtName;
    private String provinceName;
    private String departmentName;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private List<OrderItemResponse> items;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

}