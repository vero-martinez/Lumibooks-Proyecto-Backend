package com.lumibooks.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.user.request.ChangePasswordRequest;
import com.lumibooks.backend.dto.user.request.UserCreateRequest;
import com.lumibooks.backend.dto.user.request.UserProfileUpdateRequest;
import com.lumibooks.backend.dto.user.request.UserUpdateRequest;
import com.lumibooks.backend.dto.user.response.GestorSummaryResponse;
import com.lumibooks.backend.dto.user.response.UserAdminDetailResponse;
import com.lumibooks.backend.dto.user.response.UserMeResponse;
import com.lumibooks.backend.dto.user.response.UserSummaryResponse;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.UserMapper;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.security.RefreshTokenService;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.NotificationService;
import com.lumibooks.backend.service.UserService;
import com.lumibooks.backend.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de usuarios.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final RefreshTokenService refreshTokenService;

    private final NotificationService notificationService;
    private final ActionLogService actionLogService;

    // ============ Administración ============

    // Obtiene usuarios con filtros para el panel de administración.
    @Override
    public Page<UserSummaryResponse> getUsersAdmin(
            String search,
            String dni,
            String email,
            RoleUser role,
            Boolean isActive,
            Pageable pageable) {
        Specification<User> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(UserSpecification.search(search));
        }
        if (dni != null && !dni.isBlank()) {
            spec = spec.and(UserSpecification.hasDni(dni));
        }
        if (email != null && !email.isBlank()) {
            spec = spec.and(UserSpecification.hasEmail(email));
        }
        if (role != null) {
            spec = spec.and(UserSpecification.hasRole(role));
        }
        if (isActive != null) {
            spec = spec.and(UserSpecification.hasActive(isActive));
        }

        return userRepository.findAll(spec, pageable)
                .map(userMapper::toSummaryResponse);
    }

    // Obtiene la lista de gestores activos para este panel de administración.
    @Override
    public List<GestorSummaryResponse> getGestoresActivos() {
        return userRepository.findByRoleAndIsActiveTrue(RoleUser.GESTOR)
                .stream()
                .map(user -> GestorSummaryResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .build())
                .toList();
    }

    // Obtiene el detalle de un usuario para el panel de administración.
    @Override
    public UserAdminDetailResponse getUserDetailAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id));

        return userMapper.toAdminDetailResponse(user);
    }

    // Crea un nuevo usuario desde el panel de administración.
    @Override
    @Transactional
    public UserAdminDetailResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        if (userRepository.existsByDni(request.getDni())) {
            throw new BadRequestException("El DNI ya está registrado");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, encodedPassword);
        User savedUser = userRepository.save(user);

        actionLogService.log(
                ActionType.CREAR,
                EntityType.USER,
                savedUser.getId(),
                "Creó el usuario '" + savedUser.getFullName()
                        + "' con rol " + savedUser.getRole().name());

        notificationService.sendNotification(
                savedUser,
                "¡Bienvenido a LumiBooks!",
                "Hola " + savedUser.getFullName()
                        + ", gracias por unirte a LumiBooks. ¡Esperamos que disfrutes tu experiencia!");

        return userMapper.toAdminDetailResponse(savedUser);
    }

    // Actualiza los datos de un usuario existente.
    @Override
    @Transactional
    public UserAdminDetailResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id));

        userMapper.updateEntity(request, user);

        User saved = userRepository.save(user);

        actionLogService.log(
                ActionType.EDITAR,
                EntityType.USER,
                saved.getId(),
                "Editó el usuario '" + saved.getFullName() + "'");

        return userMapper.toAdminDetailResponse(saved);
    }

    // ============ Perfil propio ============

    // Obtiene el perfil del usuario autenticado.
    @Override
    public UserMeResponse getMyProfile() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return userMapper.toMeResponse(user);
    }

    // Actualiza los datos del perfil del usuario autenticado.
    @Override
    @Transactional
    public UserMeResponse updateMyProfile(UserProfileUpdateRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        userMapper.updateMeEntity(request, user);

        User saved = userRepository.save(user);

        return userMapper.toMeResponse(saved);
    }

    // Cambia la contraseña del usuario autenticado.
    @Override
    @Transactional
    public void changeMyPassword(ChangePasswordRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        // Verificar que la contraseña actual sea correcta.
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        // Guardar la nueva contraseña cifrada.
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Invalidar los tokens de acceso anteriores.
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        // Revocar las sesiones activas del usuario.
        refreshTokenService.revokeAllByUser(user.getId());
    }
}