package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.cloudinary.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    /**
     * Sube una imagen a Cloudinary en la carpeta indicada.
     *
     * @param file   archivo de imagen a subir
     * @param folder carpeta destino en Cloudinary
     * @return datos de la imagen subida
     */
    ImageUploadResponse uploadImage(MultipartFile file, String folder);

    /**
     * Elimina una imagen de Cloudinary por su publicId.
     *
     * @param publicId identificador público de la imagen en Cloudinary
     */
    void deleteImage(String publicId);

    /**
     * Reemplaza una imagen existente por una nueva.
     *
     * @param oldPublicId publicId de la imagen a eliminar
     * @param newFile     nueva imagen a subir
     * @param folder      carpeta destino en Cloudinary
     * @return datos de la nueva imagen subida
     */
    ImageUploadResponse replaceImage(String oldPublicId, MultipartFile newFile, String folder);
}