package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_providers")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(name = "full_name")
    private String fullName;

    private String phone;
    private String specialty;
    private String description;

    // La calificación de todo proveedor nuevo inicia en 5 por defecto.
    private BigDecimal rating = BigDecimal.valueOf(5);

    @Column(name = "is_validated")
    private Boolean isValidated = false;

    @Column(name = "price_per_appointment")
    private BigDecimal pricePerAppointment;

    @Column(name = "average_rating")
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private String language = "Español";

    private String modality = "Presencial";

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 30;

    private String street;
    private String district;
    private String city;
    private String country;

    @Column(name = "cedula_profesional")
    private String cedulaProfesional;

    @Column(name = "registro_medico")
    private String registroMedico;

    @ElementCollection
    @CollectionTable(name = "provider_areas_enfoque", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "area")
    private List<String> areasEnfoque = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "provider_certifications", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "certificacion")
    private List<String> certificaciones = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "provider_schedule", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "horario_item")
    private List<String> horario = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "provider_prices", joinColumns = @JoinColumn(name = "provider_id"))
    private List<PriceItem> precios = new ArrayList<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderEducation> educacion = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "provider_services",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<MedicalService> services = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "provider_clinics",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "clinic_id")
    )
    private Set<Clinic> clinics = new HashSet<>();
}