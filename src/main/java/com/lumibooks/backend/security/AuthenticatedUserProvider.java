package com.lumibooks.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Componente encargado de obtener el usuario actualmente autenticado.
 *
 * Utiliza el contexto de seguridad de Spring Security para identificar
 * al usuario mediante el email almacenado en la autenticación actual.
 */
@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    /**
     * Obtiene la entidad User del usuario autenticado.
     *
     * Spring Security guarda la información del usuario autenticado
     * después de validar correctamente el JWT.
     *
     * @return usuario autenticado
     * @throws ResourceNotFoundException si el usuario no existe en la BD
     */
    public User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"));
    }
}