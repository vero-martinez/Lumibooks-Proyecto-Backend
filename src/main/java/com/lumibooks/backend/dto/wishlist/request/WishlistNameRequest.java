package com.lumibooks.backend.dto.wishlist.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de una nueva lista de deseos y 
 * para la actualización del nombre de una lista de deseos existente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishlistNameRequest {

    @NotBlank(message = "El nombre de la lista es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

}