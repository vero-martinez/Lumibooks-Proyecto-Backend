package com.lumibooks.backend.dto.department.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUpdateRequest {

    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    private Boolean isActive;

}
