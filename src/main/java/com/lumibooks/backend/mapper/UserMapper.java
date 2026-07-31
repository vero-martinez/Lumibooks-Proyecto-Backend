package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.user.request.UserCreateRequest;
import com.lumibooks.backend.dto.user.request.UserProfileUpdateRequest;
import com.lumibooks.backend.dto.user.request.UserUpdateRequest;
import com.lumibooks.backend.dto.user.response.UserAdminDetailResponse;
import com.lumibooks.backend.dto.user.response.UserMeResponse;
import com.lumibooks.backend.dto.user.response.UserSummaryResponse;
import com.lumibooks.backend.entity.User;

/**
 * Mapper encargado de transformar entidades User en DTOs de respuesta
 * y convertir DTOs de solicitud en entidades User.
 */
@Component
public class UserMapper {

    // ============ Entity --> Response DTO ============

    public UserSummaryResponse toSummaryResponse(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dni(user.getDni())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserAdminDetailResponse toAdminDetailResponse(User user) {
        return UserAdminDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dni(user.getDni())
                .cellphone(user.getCellphone())
                .role(user.getRole())
                .isActive(user.isActive())
                .isSubscribed(user.getSubscriber() != null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserMeResponse toMeResponse(User user) {
        return UserMeResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dni(user.getDni())
                .cellphone(user.getCellphone())
                .isSubscribed(user.getSubscriber() != null)
                .build();
    }

    // ============ Request DTO --> Entity ============

    public User toEntity(UserCreateRequest request, String encodedPassword) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encodedPassword)
                .dni(request.getDni())
                .cellphone(request.getCellphone())
                .role(request.getRole())
                .build();
    }

    public void updateEntity(UserUpdateRequest request, User user) {
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(request.getCellphone()).ifPresent(user::setCellphone);
        Optional.ofNullable(request.getRole()).ifPresent(user::setRole);
        Optional.ofNullable(request.getIsActive()).ifPresent(user::setActive);
    }

    public void updateMeEntity(UserProfileUpdateRequest request, User user) {
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(request.getCellphone()).ifPresent(user::setCellphone);
    }

}