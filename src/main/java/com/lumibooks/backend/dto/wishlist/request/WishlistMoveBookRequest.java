package com.lumibooks.backend.dto.wishlist.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para mover un libro de una lista de deseos a otra. 
 * Contiene el ID de la lista de destino a la que se desea mover el libro.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishlistMoveBookRequest {

    @NotNull(message = "La lista de destino es obligatoria")
    private Long targetWishlistId;

}