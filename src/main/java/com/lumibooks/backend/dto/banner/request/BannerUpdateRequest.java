package com.lumibooks.backend.dto.banner.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase DTO para recibir los datos de un banner en las solicitudes de actualización.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerUpdateRequest {

    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String title;

    @URL(message = "La URL de la imagen debe ser válida")
    private String imageUrl;

    @Size(max = 150, message = "El texto del botón no puede superar los 150 caracteres")
    private String buttonText;

    @URL(message = "La URL del botón debe ser válida")
    private String buttonUrl;

    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 5, message = "El orden no puede ser mayor a 5")
    private Integer displayOrder;

    private Boolean isActive;

}
