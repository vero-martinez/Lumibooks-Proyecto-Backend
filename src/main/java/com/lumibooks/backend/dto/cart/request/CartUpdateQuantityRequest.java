package com.lumibooks.backend.dto.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizar la cantidad de un libro en el carrito.
 * Contiene la nueva cantidad deseada para el libro específico en el carrito.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartUpdateQuantityRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

}