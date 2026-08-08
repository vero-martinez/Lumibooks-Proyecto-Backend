package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.user.request.UserCreateRequest;
import com.lumibooks.backend.dto.user.request.UserProfileUpdateRequest;
import com.lumibooks.backend.dto.user.request.UserUpdateRequest;
import com.lumibooks.backend.dto.user.response.UserAdminDetailResponse;
import com.lumibooks.backend.dto.user.response.UserMeResponse;
import com.lumibooks.backend.dto.user.response.UserSummaryResponse;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.ResourceNotFoundException;

/**
 * Interfaz para la gestión de usuarios.
 */
public interface UserService {

    /**
     * Retorna usuarios con filtros dinámicos para la tabla de administración.
     * @param search   búsqueda por nombre, apellido o nombre completo
     * @param dni      búsqueda por DNI exacto
     * @param email    búsqueda por email
     * @param role     filtro por rol
     * @param isActive filtro por estado activo/inactivo
     * @param pageable paginación y ordenamiento
     * @return página de usuarios en formato resumen
     */
    Page<UserSummaryResponse> getUsersAdmin(
            String search,
            String dni,
            String email,
            RoleUser role,
            Boolean isActive,
            Pageable pageable);

    /**
     * Retorna el detalle completo de un usuario para el panel de administración.
     * @param id identificador del usuario
     * @return detalle completo del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    UserAdminDetailResponse getUserDetailAdmin(Long id);

    /**
     * Crea un nuevo usuario desde el panel de administración.
     * @param request datos del usuario a crear
     * @return detalle del usuario creado
     * @throws BadRequestException si el email o DNI ya existen
     */
    UserAdminDetailResponse createUser(UserCreateRequest request);

    /**
     * Actualiza un usuario existente.
     * @param id      identificador del usuario
     * @param request campos a actualizar
     * @return detalle del usuario actualizado
     * @throws ResourceNotFoundException si el usuario no existe
     * @throws BadRequestException si el email o DNI ya existen
     */
    UserAdminDetailResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Retorna el perfil del usuario autenticado.
     */
    UserMeResponse getMyProfile();

    /**
     * Actualiza los datos básicos del propio perfil del usuario autenticado.
     */
    UserMeResponse updateMyProfile(UserProfileUpdateRequest request);

}