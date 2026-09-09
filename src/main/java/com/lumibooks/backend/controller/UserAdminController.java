package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.user.request.UserCreateRequest;
import com.lumibooks.backend.dto.user.request.UserUpdateRequest;
import com.lumibooks.backend.dto.user.response.GestorSummaryResponse;
import com.lumibooks.backend.dto.user.response.UserAdminDetailResponse;
import com.lumibooks.backend.dto.user.response.UserSummaryResponse;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador administrativo para la gestión de usuarios.
 * Expone endpoints protegidos para listar, crear, editar y consultar usuarios
 * desde el panel de administración.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    /**
     * Retorna usuarios con filtros dinámicos para la tabla de administración.
     *
     * @param search   búsqueda por nombre, apellido o nombre completo
     * @param dni      búsqueda por DNI exacto
     * @param email    búsqueda por email
     * @param role     filtro por rol
     * @param isActive filtro por estado activo/inactivo
     * @param pageable paginación y ordenamiento
     * @return página de usuarios en formato resumen
     */
    @GetMapping
    public ResponseEntity<Page<UserSummaryResponse>> getUsersAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) RoleUser role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersAdmin(search, dni, email, role, isActive, pageable));
    }

    /**
     * Retorna la lista de gestores activos para selectores de asignación.
     *
     * @return lista de gestores activos
     */
    @GetMapping("/gestores")
    public ResponseEntity<List<GestorSummaryResponse>> getGestoresActivos() {
        return ResponseEntity.ok(userService.getGestoresActivos());
    }

    /**
     * Retorna el detalle completo de un usuario.
     *
     * @param id identificador del usuario
     * @return detalle completo del usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminDetailResponse> getUserDetailAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDetailAdmin(id));
    }

    /**
     * Crea un nuevo usuario desde el panel de administración.
     *
     * @param request datos del usuario a crear
     * @return detalle del usuario creado con status 201
     */
    @PostMapping
    public ResponseEntity<UserAdminDetailResponse> createUser(
            @RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id      identificador del usuario
     * @param request campos a actualizar
     * @return detalle del usuario actualizado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserAdminDetailResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

}