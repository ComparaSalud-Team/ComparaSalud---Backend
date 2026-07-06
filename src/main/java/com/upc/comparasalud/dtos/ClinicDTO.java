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
public class ClinicDTO {
    private Long id;
    private String name;
    private String ruc;
    private String description;
    private String phone;
    private String email;
    private String street;
    private String district;
    private String city;
    private String country;
    private Boolean isActive;
    private Integer providerCount;

    private List<String> specialties;

    private BigDecimal rating;
    private Integer reviewCount;

    private Boolean emergencia24h;
    private Boolean estacionamiento;
    private Boolean farmacia;
    private Boolean laboratorio;
    private Boolean imagenologia;
    private Boolean servicioAmbulancia;
    private Boolean unidadCuidadosIntensivos;
    private Boolean hospitalizacion;
    private String clinicType;
    private Integer foundedYear;
    private Integer bedsCount;
    private String emergencyPhone;
    private String website;
    private List<String> insuranceAccepted;
    private List<String> certifications;
    private List<String> schedule;
}