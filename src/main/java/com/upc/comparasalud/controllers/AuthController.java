package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.*;
import com.upc.comparasalud.services.PatientService;
import com.upc.comparasalud.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProviderService providerService;

    // HU01 - Registro de Paciente
    @PostMapping("/register/patient")
    public PatientDTO registerPatient(@RequestBody RegisterPatientRequestDTO dto) {
        return patientService.registrarPaciente(dto);
    }

    // HU12 - Registro de Proveedor
    @PostMapping("/register/provider")
    public ProviderDTO registerProvider(@RequestBody RegisterProviderRequestDTO dto) {
        return providerService.registrarProveedor(dto);
    }
}