package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.department.request.DepartmentCreateRequest;
import com.lumibooks.backend.dto.department.request.DepartmentUpdateRequest;
import com.lumibooks.backend.dto.department.response.DepartmentAdminDetailResponse;
import com.lumibooks.backend.dto.department.response.DepartmentPublicResponse;
import com.lumibooks.backend.dto.department.response.DepartmentSummaryResponse;

/**
 * Interfaz para la gestión de departamentos.
 */
public interface DepartmentService {

    /**
     * Retorna los departamentos ordenados alfabéticamente para selectores públicos.
     * @param search búsqueda opcional por nombre
     * @return lista de departamentos 
     */
    List<DepartmentPublicResponse> getDepartmentsPublic(String search);

    /**
     * Retorna departamentos con filtros dinámicos para la tabla de administración.
     * @param search   búsqueda por nombre
     * @param pageable paginación y ordenamiento
     * @return página de departamentos en formato resumen
     */
    Page<DepartmentSummaryResponse> getDepartmentsAdmin(String search, Pageable pageable);

    /**
     * Retorna el detalle completo de un departamento para el panel de administración.
     * @param id identificador del departamento
     * @return detalle completo del departamento
     * @throws ResourceNotFoundException si el departamento no existe
     */
    DepartmentAdminDetailResponse getDepartmentDetailAdmin(Long id);

    /**
     * Crea un nuevo departamento.
     * @param request datos del departamento a crear
     * @return detalle del departamento creado
     * @throws BadRequestException si ya existe un departamento con el mismo nombre
     */
    DepartmentAdminDetailResponse createDepartment(DepartmentCreateRequest request);

    /**
     * Actualiza un departamento existente.
     * @param id      identificador del departamento
     * @param request campos a actualizar
     * @return detalle del departamento actualizado
     * @throws ResourceNotFoundException si el departamento no existe
     * @throws BadRequestException si ya existe un departamento con el mismo nombre
     */
    DepartmentAdminDetailResponse updateDepartment(Long id, DepartmentUpdateRequest request);

}
