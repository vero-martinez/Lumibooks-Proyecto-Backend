package com.lumibooks.backend.dto.district.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de un nuevo distrito.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DistrictCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El nombre solo puede contener letras")
    private String name;

    @NotNull(message = "La provincia es obligatoria")
    private Long provinceId;

    @DecimalMin(value = "0.00", message = "El costo de envío no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El costo de envío debe tener máximo 2 decimales")
    private BigDecimal shippingCost;

    private Boolean isActive = true;

}
