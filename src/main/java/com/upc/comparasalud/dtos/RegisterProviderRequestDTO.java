package com.upc.comparasalud.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterProviderRequestDTO {

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

    @NotBlank(message = "Los datos colocados no son válidos")
    private String specialty;

    private String description;
    private BigDecimal pricePerAppointment;
    private Integer experienceYears;
    private String street;
    private String district;
    private String city;
    private String country;
    private String language;
    private String modality;
    private Integer durationMinutes;

    @NotEmpty(message = "Debes seleccionar al menos una clínica")
    private List<Long> clinicIds;
}