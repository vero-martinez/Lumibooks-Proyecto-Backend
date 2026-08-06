package com.lumibooks.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de UserDetailsService utilizada por Spring Security.
 *
 * Permite cargar la información del usuario desde la base de datos
 * durante el proceso de autenticación.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Busca un usuario por email y lo convierte al formato
     * UserDetails que Spring Security utiliza internamente.
     *
     * @param username email del usuario autenticado
     * @return información de autenticación del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado con email: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())

                // Spring Security utiliza este formato para validar roles.
                .authorities("ROLE_" + user.getRole().name())

                // Una cuenta inactiva no podrá autenticarse.
                .accountLocked(!user.isActive())

                .build();
    }
}