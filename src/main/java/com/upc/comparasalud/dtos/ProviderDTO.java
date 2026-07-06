package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDTO {
    private Long id;
    private Long authUserId;
    private String fullName;
    private String phone;
    private String email;
    private String specialty;
    private String description;
    private BigDecimal rating;
    private Boolean isValidated;
    private BigDecimal pricePerAppointment;
    private BigDecimal averageRating;
    private Integer experienceYears;
    private String language;
    private String modality;
    private Integer durationMinutes;

    private String street;
    private String district;
    private String city;
    private String country;

    private String cedulaProfesional;
    private String registroMedico;
    private List<String> areasEnfoque;

    private List<String> certificaciones;
    private List<String> horario;
    private List<EducationDTO> educacion;
    private List<PriceItemDTO> precios;

    private List<MedicalServiceDTO> services;

    private String photoUrl;

    private List<Long> clinicIds;
    private List<String> clinicNames;
}