package com.lumibooks.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.request.LoginRequest;
import com.lumibooks.backend.dto.request.RegisterRequest;
import com.lumibooks.backend.dto.response.ApiResponse;
import com.lumibooks.backend.dto.response.AuthResponse;
import com.lumibooks.backend.dto.response.AuthResult;
import com.lumibooks.backend.exception.UnauthorizedException;
import com.lumibooks.backend.security.RefreshTokenCookieUtil;
import com.lumibooks.backend.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado de la autenticación de usuarios.
 */
@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieUtil refreshTokenCookieUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                 HttpServletResponse response) {
        AuthResult result = authService.register(registerRequest);
        refreshTokenCookieUtil.addRefreshCookie(response, result.rawRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(result.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {
        AuthResult result = authService.login(loginRequest);
        refreshTokenCookieUtil.addRefreshCookie(response, result.rawRefreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawRefreshToken = refreshTokenCookieUtil.extractRefreshToken(request);
        if (rawRefreshToken == null) {
            throw new UnauthorizedException("No hay una sesión activa");
        }
        AuthResult result = authService.refresh(rawRefreshToken);
        refreshTokenCookieUtil.addRefreshCookie(response, result.rawRefreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;

        authService.logout(accessToken, refreshTokenCookieUtil.extractRefreshToken(request));
        refreshTokenCookieUtil.clearCookie(response);

        return ResponseEntity.ok(ApiResponse.builder()
                .message("Sesión cerrada exitosamente")
                .build());
    }
}