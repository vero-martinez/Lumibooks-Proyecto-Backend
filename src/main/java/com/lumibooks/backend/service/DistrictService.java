package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.lumibooks.backend.dto.district.request.DistrictCreateRequest;
import com.lumibooks.backend.dto.district.request.DistrictUpdateRequest;

import com.lumibooks.backend.dto.district.response.DistrictPublicResponse;
import com.lumibooks.backend.dto.district.response.DistrictAdminDetailResponse;
import com.lumibooks.backend.dto.district.response.DistrictSummaryResponse;

/**
 * Intefaz para la gestión de distritos, incluyendo operaciones para el 
 * panel de administración y selectores públicos.
 */
public interface DistrictService {

    /**
     * Retorna los distritos de una provincia ordenados alfabéticamente para selectores públicos.
     * @param provinceId identificador de la provincia
     * @param search     búsqueda opcional por nombre
     * @return lista de distritos de una provincia
     */
    List<DistrictPublicResponse> getDistrictsPublic(Long provinceId, String search);

    /**
     * Retorna distritos con filtros dinámicos para la tabla de administración.
     * @param search     búsqueda por nombre
     * @param provinceId filtro por provincia
     * @param departmentId filtro por departamento
     * @param pageable   paginación y ordenamiento
     * @return página de distritos en formato resumen
     */
    Page<DistrictSummaryResponse> getDistrictsAdmin(String search, Long provinceId, Long departmentId, Pageable pageable);

    /**
     * Retorna el detalle completo de un distrito para el panel de administración.
     * @param id identificador del distrito
     * @return detalle completo del distrito
     * @throws ResourceNotFoundException si el distrito no existe
     */
    DistrictAdminDetailResponse getDistrictDetailAdmin(Long id);

    /**
     * Crea un nuevo distrito.
     * @param request datos del distrito a crear
     * @return detalle del distrito creado
     * @throws ResourceNotFoundException si la provincia no existe
     * @throws BadRequestException si ya existe un distrito con el mismo nombre en la misma provincia
     */
    DistrictAdminDetailResponse createDistrict(DistrictCreateRequest request);

    /**
     * Actualiza un distrito existente.
     * @param id      identificador del distrito
     * @param request campos a actualizar
     * @return detalle del distrito actualizado
     * @throws ResourceNotFoundException si el distrito o la provincia no existe
     * @throws BadRequestException si ya existe un distrito con el mismo nombre en la misma provincia
     */
    DistrictAdminDetailResponse updateDistrict(Long id, DistrictUpdateRequest request);

}