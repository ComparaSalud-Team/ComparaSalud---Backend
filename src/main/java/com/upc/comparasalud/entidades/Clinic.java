package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clinics")
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private AuthUser authUser;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(unique = true, length = 20)
    private String ruc;

    @Column(length = 300)
    private String description;

    private String phone;
    private String email;

    private String street;
    private String district;
    private String city;
    private String country;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "emergencia_24h")
    private Boolean emergencia24h = false;

    private Boolean estacionamiento = false;

    private Boolean farmacia = false;

    private Boolean laboratorio = false;

    private Boolean imagenologia = false;

    @Column(name = "servicio_ambulancia")
    private Boolean servicioAmbulancia = false;

    @Column(name = "unidad_cuidados_intensivos")
    private Boolean unidadCuidadosIntensivos = false;

    private Boolean hospitalizacion = false;

    @ManyToMany(mappedBy = "clinics", fetch = FetchType.LAZY)
    private Set<Provider> providers = new HashSet<>();

    @Column(name = "clinic_type", length = 100)
    private String clinicType;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "beds_count")
    private Integer bedsCount;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    private String website;

    @Column(name = "insurance_accepted", length = 500)
    private String insuranceAccepted;

    @Column(length = 500)
    private String certifications;

    @Column(length = 500)
    private String schedule;
}