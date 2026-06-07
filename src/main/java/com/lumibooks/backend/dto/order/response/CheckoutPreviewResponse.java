package com.lumibooks.backend.dto.order.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar la vista previa del checkout, incluyendo los detalles de los items,
 * la dirección de envío seleccionada y el resumen de costos.
 */
@Getter
@Builder
public class CheckoutPreviewResponse {

    private List<OrderItemResponse> items;
    private CheckoutAddressResponse address;
    private BigDecimal subtotal;
    private BigDecimal total;

}