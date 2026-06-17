package com.lumibooks.backend.dto.banner.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase DTO para recibir los datos de un banner en las solicitudes de creación.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerCreateRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String title;

    @NotBlank(message = "El texto del botón es obligatorio")
    @Size(max = 150, message = "El texto del botón no puede superar los 150 caracteres")
    private String buttonText;

    @NotBlank(message = "La URL del botón es obligatoria")
    @URL(message = "La URL del botón debe ser válida")
    private String buttonUrl;

    @NotNull(message = "El orden de visualización es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 5, message = "El orden no puede ser mayor a 5")
    private Integer displayOrder;

    private Boolean isActive = false;

}