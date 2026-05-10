package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.PatientDTO;
import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Patient;
import com.upc.comparasalud.exceptions.*;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.PatientRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PatientService {

    private static final Set<String> ALLOWED_PHOTO_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_PHOTO_SIZE_BYTES = 5 * 1024 * 1024L; // 5 MB

    @Autowired private PatientRepository patientRepository;
    @Autowired private AuthUserRepository authUserRepository;
    @Autowired private ModelMapper modelMapper;

    @Value("${app.upload-dir:uploads/photos}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // ── HU07 – Ver perfil ────────────────────────────────────────────────────
    public PatientDTO buscarPorId(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        return toDTO(patient);
    }

    // ── HU07 – Ver perfil autenticado (GET /users/profile) ──────────────────
    public PatientDTO verPerfilPropio(String email) {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Patient patient = patientRepository.findByAuthUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de paciente no encontrado"));
        return toDTO(patient);
    }

    // ── HU08 – Editar perfil ─────────────────────────────────────────────────
    @Transactional
    public PatientDTO actualizarPaciente(Long id, PatientDTO dto, String callerEmail) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        // Ownership check (HU08 – RLS)
        if (!patient.getAuthUser().getEmail().equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para modificar este perfil");
        }

        // Validate required fields (HU08 – 400)
        if (!StringUtils.hasText(dto.getName()) || !StringUtils.hasText(dto.getPhone())) {
            throw new BadRequestException("Los datos colocados no son válidos");
        }

        // Phone uniqueness excluding self (HU08 – 409)
        if (patientRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new ConflictException("El número telefónico ya está registrado en otra cuenta");
        }

        patient.setName(dto.getName());
        patient.setPhone(dto.getPhone());
        patient.setCountry(dto.getCountry());
        patient = patientRepository.save(patient);

        return toDTO(patient);
    }

    // ── HU09 – Subir foto de perfil ──────────────────────────────────────────
    @Transactional
    public PatientDTO subirFotoPerfil(Long id, MultipartFile file, String callerEmail) throws IOException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        // Ownership
        if (!patient.getAuthUser().getEmail().equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para modificar este perfil");
        }

        // Format validation
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_PHOTO_TYPES.contains(contentType)) {
            throw new BadRequestException("Formato de imagen no permitido. Use JPG, PNG o WEBP.");
        }

        // Size validation
        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            throw new BadRequestException("El archivo supera el tamaño máximo permitido (5 MB).");
        }

        // Persist file
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String extension  = contentType.split("/")[1];
        String filename   = UUID.randomUUID() + "." + extension;
        Path   targetPath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String photoUrl = baseUrl + "/uploads/photos/" + filename;
        patient.getAuthUser().setProfilePhotoUrl(photoUrl);
        authUserRepository.save(patient.getAuthUser());

        return toDTO(patient);
    }

    // ── Admin – Listar todos ─────────────────────────────────────────────────
    public List<PatientDTO> listarTodos() {
        return patientRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // ── Private helper ───────────────────────────────────────────────────────
    private PatientDTO toDTO(Patient patient) {
        PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
        dto.setEmail(patient.getAuthUser().getEmail());
        dto.setAuthUserId(patient.getAuthUser().getId());
        return dto;
    }
}