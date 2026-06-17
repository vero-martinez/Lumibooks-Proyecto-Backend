package com.lumibooks.backend.dto.cloudinary;

import lombok.Builder;
import lombok.Data;

/**
 * DTO que representa la respuesta tras subir una imagen a Cloudinary.
 * Contiene la información retornada por la API de Cloudinary.
 */
@Data
@Builder
public class ImageUploadResponse {
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private long bytes;
}