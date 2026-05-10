package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.PatientDTO;
import com.upc.comparasalud.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // HU07 – Ver perfil propio (GET /users/profile)
    @GetMapping("/users/profile")
    public ResponseEntity<PatientDTO> verPerfilPropio(Principal principal) {
        return ResponseEntity.ok(patientService.verPerfilPropio(principal.getName()));
    }

    // HU07 – Ver perfil por ID (admin o uso interno)
    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.buscarPorId(id));
    }

    // Admin – Listar todos
    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> listarTodos() {
        return ResponseEntity.ok(patientService.listarTodos());
    }

    // HU08 – Editar perfil (PUT /users/profile con ID, o PUT /patients/{id})
    @PutMapping("/patients/{id}")
    public ResponseEntity<PatientDTO> actualizar(
            @PathVariable Long id,
            @RequestBody PatientDTO patientDTO,
            Principal principal) {
        return ResponseEntity.ok(patientService.actualizarPaciente(id, patientDTO, principal.getName()));
    }

    // HU09 – Subir foto de perfil
    @PostMapping(value = "/patients/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        try {
            PatientDTO updated = patientService.subirFotoPerfil(id, file, principal.getName());
            return ResponseEntity.ok(Map.of(
                    "message", "Perfil actualizado correctamente",
                    "profilePhotoUrl", updated.getEmail() // adjusted: real URL is in AuthUser
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudo guardar la imagen. Intenta nuevamente."));
        }
    }
}