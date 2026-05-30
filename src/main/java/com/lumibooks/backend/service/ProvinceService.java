package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.province.request.ProvinceCreateRequest;
import com.lumibooks.backend.dto.province.request.ProvinceUpdateRequest;
import com.lumibooks.backend.dto.province.response.ProvinceAdminDetailResponse;
import com.lumibooks.backend.dto.province.response.ProvincePublicResponse;
import com.lumibooks.backend.dto.province.response.ProvinceSummaryResponse;

/**
 * Intefaz para la gestión de provincias
 */
public interface ProvinceService {

    /**
     * Retorna las provincias activas de un departamento ordenadas alfabéticamente para selectores públicos.
     * @param departmentId identificador del departamento
     * @param search       búsqueda opcional por nombre
     * @return lista de provincias activas del departamento
     */
    List<ProvincePublicResponse> getProvincesPublic(Long departmentId, String search);

    /**
     * Retorna provincias con filtros dinámicos para la tabla de administración.
     * @param search       búsqueda por nombre
     * @param isActive     filtro por estado activo/inactivo
     * @param departmentId filtro por departamento
     * @param pageable     paginación y ordenamiento
     * @return página de provincias en formato resumen
     */
    Page<ProvinceSummaryResponse> getProvincesAdmin(String search, Boolean isActive, Long departmentId, Pageable pageable);

    /**
     * Retorna el detalle completo de una provincia para el panel de administración.
     * @param id identificador de la provincia
     * @return detalle completo de la provincia
     * @throws ResourceNotFoundException si la provincia no existe
     */
    ProvinceAdminDetailResponse getProvinceDetailAdmin(Long id);

    /**
     * Crea una nueva provincia.
     * @param request datos de la provincia a crear
     * @return detalle de la provincia creada
     * @throws ResourceNotFoundException si el departamento no existe
     * @throws BadRequestException si se intenta crear una provincia activa en un departamento inactivo
     * @throws BadRequestException si ya existe una provincia con el mismo nombre en el mismo departamento
     */
    ProvinceAdminDetailResponse createProvince(ProvinceCreateRequest request);

    /**
     * Actualiza una provincia existente.
     * @param id      identificador de la provincia
     * @param request campos a actualizar
     * @return detalle de la provincia actualizada
     * @throws ResourceNotFoundException si la provincia o el departamento no existe
     * @throws BadRequestException si se intenta activar una provincia con departamento inactivo
     * @throws BadRequestException si ya existe una provincia con el mismo nombre en el mismo departamento
     */
    ProvinceAdminDetailResponse updateProvince(Long id, ProvinceUpdateRequest request);

}
