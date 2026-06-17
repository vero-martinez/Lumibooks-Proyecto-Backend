package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.banner.request.BannerCreateRequest;
import com.lumibooks.backend.dto.banner.request.BannerUpdateRequest;
import com.lumibooks.backend.dto.banner.response.BannerAdminDetailResponse;
import com.lumibooks.backend.dto.banner.response.BannerPublicResponse;
import com.lumibooks.backend.dto.banner.response.BannerSummaryResponse;
import com.lumibooks.backend.exception.ResourceNotFoundException;

/**
 * Interfaz para la gestión de banners.
 */
public interface BannerService {

    /**
     * Retorna los banners activos ordenados por displayOrder para el carrusel
     * público.
     * 
     * @return lista de banners activos
     */
    List<BannerPublicResponse> getBannersPublic();

    /**
     * Retorna banners con filtros dinámicos para la tabla de administración.
     * 
     * @param search   búsqueda por título
     * @param isActive filtro por estado activo/inactivo
     * @param pageable paginación y ordenamiento
     * @return página de banners en formato resumen
     */
    Page<BannerSummaryResponse> getBannersAdmin(String search, Boolean isActive, Pageable pageable);

    /**
     * Retorna el detalle completo de un banner para el panel de administración.
     * 
     * @param id identificador del banner
     * @return detalle completo del banner
     * @throws ResourceNotFoundException si el banner no existe
     */
    BannerAdminDetailResponse getBannerDetailAdmin(Long id);

    /**
     * Crea un nuevo banner.
     * 
     * @param request datos del banner a crear
     * @return detalle del banner creado
     */
    BannerAdminDetailResponse createBanner(BannerCreateRequest request, MultipartFile image);

    /**
     * Actualiza un banner existente.
     * 
     * @param id identificador del banner
     * @param request campos a actualizar
     * @return detalle del banner actualizado
     * @throws ResourceNotFoundException si el banner no existe
     * @throws BadRequestException si se intenta activar un banner sin especificar displayOrder 
     */
    BannerAdminDetailResponse updateBanner(Long id, BannerUpdateRequest request, MultipartFile image);

    /**
     * Elimina físicamente un banner.
     * 
     * @param id identificador del banner
     * @throws ResourceNotFoundException si el banner no existe
     */
    void deleteBanner(Long id);

}