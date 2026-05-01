package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.PatientDTO;
import com.upc.comparasalud.dtos.RegisterPatientRequestDTO;
import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Patient;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.PatientRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public PatientDTO registrarPaciente(RegisterPatientRequestDTO dto) {
        AuthUser authUser = new AuthUser();
        authUser.setEmail(dto.getEmail());
        authUser.setPassword(dto.getPassword());
        authUser.setRoleId(1);
        authUser.setIsVerified(false);
        authUser.setCreatedAt(LocalDateTime.now());
        authUser = authUserRepository.save(authUser);

        Patient patient = new Patient();
        patient.setAuthUser(authUser);
        patient.setName(dto.getName());
        patient.setPhone(dto.getPhone());
        // parsear birthday si viene
        if (dto.getBirthday() != null && !dto.getBirthday().isBlank()) {
            patient.setBirthday(LocalDate.parse(dto.getBirthday()));
        }
        patient.setCountry(dto.getCountry());
        patient = patientRepository.save(patient);

        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);
        patientDTO.setEmail(authUser.getEmail());
        patientDTO.setAuthUserId(authUser.getId());
        return patientDTO;
    }

    public List<PatientDTO> listarTodos() {
        return patientRepository.findAll().stream()
                .map(patient -> {
                    PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
                    dto.setEmail(patient.getAuthUser().getEmail());
                    dto.setAuthUserId(patient.getAuthUser().getId());
                    return dto;
                })
                .toList();
    }

    public PatientDTO buscarPorId(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
        dto.setEmail(patient.getAuthUser().getEmail());
        dto.setAuthUserId(patient.getAuthUser().getId());
        return dto;
    }

    @Transactional
    public PatientDTO actualizarPaciente(Long id, PatientDTO dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        patient.setName(dto.getName());
        patient.setPhone(dto.getPhone());
        patient.setCountry(dto.getCountry());

        patient = patientRepository.save(patient);

        PatientDTO response = modelMapper.map(patient, PatientDTO.class);
        response.setEmail(patient.getAuthUser().getEmail());
        response.setAuthUserId(patient.getAuthUser().getId());
        return response;
    }
}