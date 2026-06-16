package com.lumibooks.backend.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String title;

    @NotNull(message = "La imagen de portada es obligatoria")
    private MultipartFile coverImage;

    @NotEmpty(message = "El libro debe tener al menos un autor")
    private Set<Long> authorIds;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    private String description;

    @NotBlank(message = "El ISBN es obligatorio")
    @Pattern(regexp = "^[0-9]{13}$", message = "El ISBN debe tener exactamente 13 dígitos")
    private String isbn;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El número de páginas es obligatorio")
    @Min(value = 1, message = "El número de páginas tiene que ser mayor a 1")
    private Integer pageCount;

    @NotNull(message = "El idioma es obligatorio")
    private BookLanguage language;

    @NotNull(message = "El formato es obligatorio")
    private BookFormat format;

    @Min(value = 1450, message = "El año de edición no puede ser menor a 1450")
    @Max(value = 2100, message = "El año de edición no puede ser mayor a 2100")
    private Short editionYear;

    @NotNull(message = "La editorial es obligatoria")
    private Long publisherId;

    @NotEmpty(message = "El libro debe tener al menos una categoría")
    private Set<Long> categoryIds;

}
