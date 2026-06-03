package com.lumibooks.backend.dto.address.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de una nueva dirección.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressCreateRequest {

    @NotNull(message = "El distrito es obligatorio")
    private Long districtId;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String addressLine;

    @Size(max = 255, message = "La referencia no puede superar los 255 caracteres")
    private String reference;

}