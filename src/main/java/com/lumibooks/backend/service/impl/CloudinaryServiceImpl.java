package com.lumibooks.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lumibooks.backend.dto.cloudinary.ImageUploadResponse;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Implementación del servicio de gestión de imágenes en Cloudinary.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public ImageUploadResponse uploadImage(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"));
            return ImageUploadResponse.builder()
                    .publicId((String) result.get("public_id"))
                    .url((String) result.get("url"))
                    .secureUrl((String) result.get("secure_url"))
                    .format((String) result.get("format"))
                    .bytes(((Number) result.get("bytes")).longValue())
                    .build();
        } catch (IOException e) {
            throw new BadRequestException("Error al subir imagen: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new BadRequestException("Error al eliminar imagen: " + e.getMessage());
        }
    }

    @Override
    public ImageUploadResponse replaceImage(String oldPublicId, MultipartFile newFile, String folder) {
        if (oldPublicId != null && !oldPublicId.isBlank()) {
            deleteImage(oldPublicId);
        }
        return uploadImage(newFile, folder);
    }
}