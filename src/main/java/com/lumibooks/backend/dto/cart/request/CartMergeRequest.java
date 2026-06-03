package com.lumibooks.backend.dto.cart.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa el carrito anónimo a fusionar con el carrito del usuario.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartMergeRequest {

    @NotEmpty(message = "La lista de items no puede estar vacía")
    private List<CartAddItemRequest> items;

}