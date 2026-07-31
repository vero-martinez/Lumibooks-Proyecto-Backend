package com.lumibooks.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.user.request.UserProfileUpdateRequest;
import com.lumibooks.backend.dto.user.response.UserMeResponse;
import com.lumibooks.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado del perfil del usuario autenticado.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    /**
     * Retorna el perfil del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<UserMeResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    /**
     * Actualiza los datos básicos del propio perfil del usuario autenticado.
     */
    @PatchMapping
    public ResponseEntity<UserMeResponse> updateMyProfile(
            @RequestBody @Valid UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

}