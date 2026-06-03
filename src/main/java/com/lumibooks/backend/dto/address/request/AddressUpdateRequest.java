package com.lumibooks.backend.dto.address.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la actualización de una dirección existente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressUpdateRequest {

    private Long districtId;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String addressLine;

    @Size(max = 255, message = "La referencia no puede superar los 255 caracteres")
    private String reference;
    
}
