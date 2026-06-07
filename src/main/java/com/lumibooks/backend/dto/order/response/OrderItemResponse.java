package com.lumibooks.backend.dto.order.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO para mostrar los detalles de cada item de un pedido, 
 * utilizado tanto en el detalle del pedido para el cliente y el administrador 
 * como en la vista previa del checkout.
 */
@Getter
@Builder
public class OrderItemResponse {

    private Long bookId;
    private String coverImageUrl;
    private String title;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

}