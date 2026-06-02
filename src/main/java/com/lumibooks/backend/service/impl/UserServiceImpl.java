package com.lumibooks.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.user.request.UserCreateRequest;
import com.lumibooks.backend.dto.user.request.UserUpdateRequest;
import com.lumibooks.backend.dto.user.response.UserAdminDetailResponse;
import com.lumibooks.backend.dto.user.response.UserSummaryResponse;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.UserMapper;
import com.lumibooks.backend.repository.UserRepository;
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

    // ============ Admin ============

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

    @Override
    public UserAdminDetailResponse getUserDetailAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id));
        return userMapper.toAdminDetailResponse(user);
    }

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
        return userMapper.toAdminDetailResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserAdminDetailResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id));

        userMapper.updateEntity(request, user);
        return userMapper.toAdminDetailResponse(userRepository.save(user));
    }

}