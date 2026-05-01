package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.PatientDTO;
import com.upc.comparasalud.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Listar todos los pacientes (solo admin)
    @GetMapping("/patients")
    public List<PatientDTO> listarTodos() {
        return patientService.listarTodos();
    }

    // Obtener paciente por ID (HU07 - Ver perfil)
    @GetMapping("/patients/{id}")
    public PatientDTO buscarPorId(@PathVariable Long id) {
        return patientService.buscarPorId(id);
    }

    // Actualizar perfil del paciente (HU08 - Editar perfil)
    @PutMapping("/patients/{id}")
    public PatientDTO actualizar(@PathVariable Long id, @RequestBody PatientDTO patientDTO) {
        return patientService.actualizarPaciente(id, patientDTO);
    }
}