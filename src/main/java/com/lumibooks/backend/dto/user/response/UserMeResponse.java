package com.lumibooks.backend.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa la información del perfil del usuario autenticado.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private String cellphone;
    private Boolean isSubscribed;

}