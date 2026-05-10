package com.upc.comparasalud.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterPatientRequestDTO {

    @NotBlank(message = "Los datos colocados no son válidos")
    @Email(message = "Los datos colocados no son válidos")
    private String email;

    @NotBlank(message = "Los datos colocados no son válidos")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "Los datos colocados no son válidos")
    private String name;

    @NotBlank(message = "Los datos colocados no son válidos")
    private String phone;

    private String birthday;  // optional, "YYYY-MM-DD"
    private String country;   // optional
}