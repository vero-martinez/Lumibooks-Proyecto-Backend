package com.lumibooks.backend.dto.province.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la actualización de una provincia existente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceUpdateRequest {

    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    private Long departmentId;

}
