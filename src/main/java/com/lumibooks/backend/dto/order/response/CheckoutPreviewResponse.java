package com.lumibooks.backend.dto.order.response;

import java.math.BigDecimal;
import java.util.List;

import com.lumibooks.backend.dto.address.response.AddressResponse;

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
    private AddressResponse address;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal total;

}