package com.lumibooks.backend.dto.response;

public record AuthResult(AuthResponse response, String rawRefreshToken) {}