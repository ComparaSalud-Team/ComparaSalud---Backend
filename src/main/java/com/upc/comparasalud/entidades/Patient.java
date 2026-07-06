package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users_patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AuthUser authUser;

    private String name;
    private String phone;
    private LocalDate birthday;
    private String country;
    private String dni;
    private String estadoCivil;
    private String profesion;
    private String idiomaPreferido;
    private String direccion;
    private String genero;

    private String tipoSangre;
    private String alergias;
    private String condicionesMedicas;
    private String medicamentosActuales;
    private String seguroMedicoNombre;
    private String seguroMedicoPlan;

    private String emergenciaNombre;
    private String emergenciaParentesco;
    private String emergenciaTelefono;
    private String emergenciaDireccion;
}
