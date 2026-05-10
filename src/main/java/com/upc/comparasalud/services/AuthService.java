package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.*;
import com.upc.comparasalud.entidades.*;
import com.upc.comparasalud.exceptions.*;
import com.upc.comparasalud.repositorios.*;
import com.upc.comparasalud.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    @Autowired private AuthUserRepository authUserRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private PasswordResetTokenRepository resetTokenRepository;
    @Autowired private EmailVerificationTokenRepository verificationTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EmailService emailService;

    // ── HU01 – Registro de paciente ──────────────────────────────────────────
    @Transactional
    public PatientDTO registrarPaciente(RegisterPatientRequestDTO dto) {

        if (authUserRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("El email ya está registrado en otra cuenta");
        }
        if (patientRepository.existsByPhone(dto.getPhone())) {
            throw new ConflictException("El número telefónico ya está registrado en otra cuenta");
        }

        AuthUser authUser = new AuthUser();
        authUser.setEmail(dto.getEmail());
        authUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        authUser.setRoleId(1);
        authUser.setIsVerified(false);
        authUser.setCreatedAt(LocalDateTime.now());
        authUser = authUserRepository.save(authUser);

        Patient patient = new Patient();
        patient.setAuthUser(authUser);
        patient.setName(dto.getName());
        patient.setPhone(dto.getPhone());
        if (dto.getBirthday() != null && !dto.getBirthday().isBlank()) {
            patient.setBirthday(LocalDate.parse(dto.getBirthday()));
        }
        patient.setCountry(dto.getCountry());
        patient = patientRepository.save(patient);

        // Disparar flujo HU06 – envío de correo de verificación
        enviarCorreoVerificacion(authUser);

        PatientDTO response = new PatientDTO();
        response.setId(patient.getId());
        response.setAuthUserId(authUser.getId());
        response.setName(patient.getName());
        response.setPhone(patient.getPhone());
        response.setEmail(authUser.getEmail());
        response.setCountry(patient.getCountry());
        return response;
    }

    // ── HU12 – Registro de proveedor ─────────────────────────────────────────
    @Transactional
    public ProviderDTO registrarProveedor(RegisterProviderRequestDTO dto) {

        if (authUserRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("El email ya está registrado en otra cuenta");
        }

        AuthUser authUser = new AuthUser();
        authUser.setEmail(dto.getEmail());
        authUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        authUser.setRoleId(2);
        authUser.setIsVerified(false);
        authUser.setCreatedAt(LocalDateTime.now());
        authUser = authUserRepository.save(authUser);

        Provider provider = new Provider();
        provider.setAuthUser(authUser);
        provider.setFullName(dto.getName());
        provider.setPhone(dto.getPhone());
        provider.setSpecialty(dto.getSpecialty());
        provider.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        provider = providerRepository.save(provider);

        enviarCorreoVerificacion(authUser);

        ProviderDTO response = new ProviderDTO();
        response.setId(provider.getId());
        response.setAuthUserId(authUser.getId());
        response.setFullName(provider.getFullName());
        response.setPhone(provider.getPhone());
        response.setEmail(authUser.getEmail());
        response.setSpecialty(provider.getSpecialty());
        response.setDescription(provider.getDescription());
        response.setRating(provider.getRating());
        response.setIsValidated(provider.getIsValidated());
        return response;
    }

    // ── HU02 – Login ─────────────────────────────────────────────────────────
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {

        AuthUser user = authUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("Los datos colocados no son válidos"));

        // Check account lock (HU02 – bloqueo por intentos fallidos)
        if (user.isLocked()) {
            throw new ForbiddenException("Cuenta bloqueada temporalmente. Intenta de nuevo en " + LOCK_MINUTES + " minutos.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                user.setFailedLoginAttempts(0);
            }
            authUserRepository.save(user);
            throw new BadRequestException("Los datos colocados no son válidos");
        }

        // Successful login → reset counter
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        authUserRepository.save(user);

        String role = resolveRole(user.getRoleId());
        String token = jwtUtil.generateToken(user.getEmail(), role, user.getId());

        return new LoginResponseDTO(token, role, user.getId(), user.getEmail());
    }

    // ── HU05 – Logout ────────────────────────────────────────────────────────
    // JWT is stateless; logout is handled client-side (delete token).
    // This endpoint returns 200 to confirm the action.
    public void logout() {
        // No server-side state to invalidate in a stateless JWT architecture.
        // A production implementation would add the token to a Redis blacklist here.
    }

    // ── HU06 – Verificar email ───────────────────────────────────────────────
    @Transactional
    public String verificarEmail(String token) {

        EmailVerificationToken verToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token de verificación inválido o ya utilizado"));

        if (verToken.getUsed()) {
            throw new BadRequestException("El enlace de verificación ya fue utilizado");
        }
        if (verToken.isExpired()) {
            throw new BadRequestException("El enlace de verificación ha expirado. Solicita uno nuevo.");
        }

        AuthUser user = verToken.getAuthUser();
        user.setIsVerified(true);
        authUserRepository.save(user);

        verToken.setUsed(true);
        verificationTokenRepository.save(verToken);

        return "Cuenta verificada correctamente";
    }

    // ── HU03 – Forgot password ───────────────────────────────────────────────
    @Transactional
    public String forgotPassword(ForgotPasswordRequestDTO dto) {
        // Always return generic message to avoid user enumeration
        authUserRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            resetTokenRepository.deleteByAuthUserId(user.getId());

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setAuthUser(user);
            resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
            resetToken.setUsed(false);
            resetTokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
        return "Si el correo existe, recibirás instrucciones para restablecer tu contraseña";
    }

    // ── HU03 – Reset password ────────────────────────────────────────────────
    @Transactional
    public String resetPassword(ResetPasswordRequestDTO dto) {

        PasswordResetToken resetToken = resetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new BadRequestException("Token inválido o ya utilizado"));

        if (resetToken.getUsed()) {
            throw new BadRequestException("Este enlace ya fue utilizado");
        }
        if (resetToken.isExpired()) {
            throw new BadRequestException("El enlace ha expirado. Solicita uno nuevo.");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
            throw new BadRequestException("La contraseña debe tener al menos 8 caracteres");
        }

        AuthUser user = resetToken.getAuthUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        authUserRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        return "Contraseña actualizada correctamente";
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    private void enviarCorreoVerificacion(AuthUser user) {
        String token = UUID.randomUUID().toString();
        verificationTokenRepository.deleteByAuthUserId(user.getId());

        EmailVerificationToken verToken = new EmailVerificationToken();
        verToken.setToken(token);
        verToken.setAuthUser(user);
        verToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        verToken.setUsed(false);
        verificationTokenRepository.save(verToken);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    private String resolveRole(Integer roleId) {
        return switch (roleId) {
            case 1 -> "PATIENT";
            case 2 -> "PROVIDER";
            case 3 -> "ADMIN";
            default -> "UNKNOWN";
        };
    }
}