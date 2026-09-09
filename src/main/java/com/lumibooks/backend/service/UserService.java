package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.user.request.ChangePasswordRequest;
import com.lumibooks.backend.dto.user.request.UserCreateRequest;
import com.lumibooks.backend.dto.user.request.UserProfileUpdateRequest;
import com.lumibooks.backend.dto.user.request.UserUpdateRequest;
import com.lumibooks.backend.dto.user.response.GestorSummaryResponse;
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
     * Obtiene una página de usuarios para el panel de administración,
     * aplicando los filtros y criterios de ordenamiento indicados.
     *
     * @param search   búsqueda por nombre, apellido o nombre completo
     * @param dni      búsqueda por DNI exacto
     * @param email    búsqueda por correo electrónico
     * @param role     filtro por rol
     * @param isActive filtro por estado activo o inactivo
     * @param pageable paginación y ordenamiento
     * @return página de usuarios en formato resumido
     */
    Page<UserSummaryResponse> getUsersAdmin(
            String search,
            String dni,
            String email,
            RoleUser role,
            Boolean isActive,
            Pageable pageable);

    /**
     * Obtiene la lista de gestores activos para el panel de administración.
     * Usado en selectores de asignación (p.ej. asignar un gestor a una orden).
     *
     * @return lista de gestores activos
     */
    List<GestorSummaryResponse> getGestoresActivos();
    /**
     * Obtiene el detalle completo de un usuario para el panel de administración.
     *
     * @param id identificador del usuario
     * @return información detallada del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    UserAdminDetailResponse getUserDetailAdmin(Long id);

    /**
     * Crea un nuevo usuario desde el panel de administración.
     *
     * @param request datos del usuario a crear
     * @return información detallada del usuario creado
     * @throws BadRequestException si el correo electrónico o DNI ya existen
     */
    UserAdminDetailResponse createUser(UserCreateRequest request);

    /**
     * Actualiza los datos de un usuario existente desde el panel de administración.
     *
     * @param id      identificador del usuario
     * @param request datos a actualizar
     * @return información detallada del usuario actualizado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    UserAdminDetailResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @return información del perfil del usuario autenticado
     */
    UserMeResponse getMyProfile();

    /**
     * Actualiza los datos básicos del perfil del usuario autenticado.
     *
     * @param request datos del perfil a actualizar
     * @return información actualizada del perfil
     */
    UserMeResponse updateMyProfile(UserProfileUpdateRequest request);

    /**
     * Cambia la contraseña del usuario autenticado.
     * Verifica la contraseña actual e invalida las sesiones existentes
     * después de realizar el cambio.
     *
     * @param request contraseña actual y nueva contraseña
     * @throws BadRequestException si la contraseña actual es incorrecta
     */
    void changeMyPassword(ChangePasswordRequest request);

}