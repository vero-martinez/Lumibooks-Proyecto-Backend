package com.lumibooks.backend.dto.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para agregar un libro al carrito. 
 * Contiene el ID del libro y la cantidad deseada.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartAddItemRequest {

    @NotNull(message = "El libro es obligatorio")
    private Long bookId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

}