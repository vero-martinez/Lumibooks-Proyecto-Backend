package com.lumibooks.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Componente que provee el usuario autenticado en el contexto de seguridad actual.
 */
@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    /**
     * Retorna la entidad User del usuario autenticado en la sesión actual.
     * @return usuario autenticado
     * @throws ResourceNotFoundException si el usuario no existe en la BD
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

}