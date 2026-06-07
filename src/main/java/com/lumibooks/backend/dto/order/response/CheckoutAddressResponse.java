package com.lumibooks.backend.dto.order.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información de la dirección de envío seleccionada 
 * y sus costos asociados durante el proceso de checkout.
 */
@Getter
@Builder
public class CheckoutAddressResponse {

    private Long id;
    private String addressLine;
    private String reference;
    private String districtName;
    private String provinceName;
    private String departmentName;
    private BigDecimal shippingCost;
    private boolean isShippingAvailable;

}