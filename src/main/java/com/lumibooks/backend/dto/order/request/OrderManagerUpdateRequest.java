package com.lumibooks.backend.dto.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la actualización del gestor asignado a una orden.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderManagerUpdateRequest {

    @NotNull(message = "El gestor es obligatorio")
    private Long managerId;

}