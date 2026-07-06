package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private Long authUserId;
    private String name;
    private String phone;
    private String email;
    private String country;
    private String birthday;

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