package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.*;
import com.upc.comparasalud.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // HU01 – Registro de paciente
    @PostMapping("/register/patient")
    public ResponseEntity<PatientDTO> registerPatient(@Valid @RequestBody RegisterPatientRequestDTO dto) {
        return ResponseEntity.ok(authService.registrarPaciente(dto));
    }

    // HU12 – Registro de proveedor
    @PostMapping("/register/provider")
    public ResponseEntity<ProviderDTO> registerProvider(@Valid @RequestBody RegisterProviderRequestDTO dto) {
        return ResponseEntity.ok(authService.registrarProveedor(dto));
    }

    // HU02 – Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // HU05 – Logout (stateless: client must discard the token)
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }

    // HU06 – Verificar email
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        String msg = authService.verificarEmail(token);
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // HU03 – Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        String msg = authService.forgotPassword(dto);
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // HU03 – Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        String msg = authService.resetPassword(dto);
        return ResponseEntity.ok(Map.of("message", msg));
    }
}