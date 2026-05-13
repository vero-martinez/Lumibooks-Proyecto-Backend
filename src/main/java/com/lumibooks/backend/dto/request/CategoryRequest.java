package com.lumibooks.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación y actualización de una categoría.
 * Contiene los datos necesarios enviados desde el cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    /**
     * El nombre de la categoría.
     * No puede estar vacío y tiene un máximo de 150 caracteres.
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;
    
}
