package com.lumibooks.backend.dto.wishlist.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la respuesta de una lista de deseos. 
 * Contiene el ID de la lista, su nombre y la cantidad de libros que contiene.
 */
@Getter
@Builder
public class WishlistResponse {

    private Long id;
    private String name;
    private Integer itemCount;

}