package com.lumibooks.backend.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {

    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String title;

    @Size(min = 1, message = "Debe tener al menos un autor")
    private Set<Long> authorIds;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    private String description;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 1, message = "El número de páginas tiene que ser mayor a 1")
    private Integer pageCount;

    private BookLanguage language;

    private BookFormat format;

    @Min(value = 1450, message = "El año de edición no puede ser menor a 1450")
    @Max(value = 2100, message = "El año de edición no puede ser mayor a 2100")
    private Short editionYear;

    private Long publisherId;

    @Size(min = 1, message = "Debe tener al menos una categoría")
    private Set<Long> categoryIds;
    
}
