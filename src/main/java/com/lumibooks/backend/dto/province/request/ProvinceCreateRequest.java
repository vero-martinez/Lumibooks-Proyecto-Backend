package com.lumibooks.backend.dto.province.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de una nueva provincia.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @NotNull(message = "El departamento es obligatorio")
    private Long departmentId;

}