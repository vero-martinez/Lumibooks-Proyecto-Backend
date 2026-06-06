package com.lumibooks.backend.dto.order.request;

import com.lumibooks.backend.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la actualización del estado de una orden.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequest {

    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;

}