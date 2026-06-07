package com.lumibooks.backend.dto.review.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la edición de una reseña
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateRequest {

    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    private String comment;

}