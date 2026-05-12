package com.lumibooks.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de UserDetailsService para cargar los detalles del usuario desde la base de datos.
 * Funciones principales:
 * - Cargar un usuario por su email (username) para la autenticación.
 * - Convertir la entidad User a un objeto UserDetails que Spring Security pueda utilizar para la autenticación y autorización.
 * - Manejar casos donde el usuario no se encuentra lanzando una excepción adecuada.
 */
@Service
@RequiredArgsConstructor // Anotación de Lombok para generar un constructor con los campos finales
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    // Cargar un usuario por su email (username) para la autenticación
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + username));

        return org.springframework.security.core.userdetails.User.builder() // Convertir la entidad User a un objeto UserDetails
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRol().name()) // Aquí se podrían agregar roles o permisos si se implementan
                .accountLocked(!user.getActivo()) // Si el campo "activo" es false, la cuenta se considera bloqueada
                .build();
    }
}