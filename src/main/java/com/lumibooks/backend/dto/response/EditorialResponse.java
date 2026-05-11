package com.lumibooks.backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
/**
 * DTO para enviar los datos de una editorial al cliente.
 * Contiene la información que se desea exponer sobre una editorial.
 */
public class EditorialResponse {

    private Long id;
    private String nombre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
}