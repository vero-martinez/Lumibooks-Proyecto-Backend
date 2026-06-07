package com.lumibooks.backend.dto.review.request;

import com.lumibooks.backend.enums.ReviewStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el cambio de estado de una reseña (pendiente, moderada, oculta)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewStatusUpdateRequest {

    @NotNull(message = "El estado es obligatorio")
    private ReviewStatus status;

}