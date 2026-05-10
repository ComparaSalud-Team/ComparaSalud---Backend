package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.AppointmentRequestDTO;
import com.upc.comparasalud.dtos.AppointmentResponseDTO;
import com.upc.comparasalud.dtos.RescheduleAppointmentRequestDTO;
import com.upc.comparasalud.entidades.Appointment;
import com.upc.comparasalud.entidades.Patient;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.exceptions.*;
import com.upc.comparasalud.repositorios.AppointmentRepository;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.PatientRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private AuthUserRepository authUserRepository;

    // ── HU33 – Agendar cita ──────────────────────────────────────────────────
    @Transactional
    public AppointmentResponseDTO agendarCita(AppointmentRequestDTO request) {

        // Basic validations
        if (request.getDate() == null || request.getStartTime() == null || request.getEndTime() == null) {
            throw new BadRequestException("Los datos colocados no son válidos");
        }
        if (!request.getDate().isAfter(LocalDate.now().minusDays(1))) {
            throw new BadRequestException("La fecha de la cita no puede ser en el pasado");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + request.getPatientId()));

        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + request.getProviderId()));

        boolean existeConflicto = appointmentRepository.existsOverlappingAppointment(
                request.getProviderId(), request.getDate(),
                request.getStartTime(), request.getEndTime());
        if (existeConflicto) {
            throw new ConflictException("El proveedor ya tiene una cita programada en ese horario");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setProvider(provider);
        appointment.setServiceName(request.getServiceName());
        appointment.setDate(request.getDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus("PENDING");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        return buildResponse(appointment, "Cita agendada correctamente");
    }

    // ── HU34 – Cancelar cita ─────────────────────────────────────────────────
    // Changed to UPDATE status (not DELETE), uses PUT, checks ownership & duplicates
    @Transactional
    public AppointmentResponseDTO cancelarCita(Long appointmentId, String callerEmail) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + appointmentId));

        // Ownership check
        checkOwnership(appointment, callerEmail);

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new ConflictException("La cita ya se encuentra cancelada");
        }
        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new BadRequestException("No se puede cancelar una cita que ya fue completada");
        }

        appointment.setStatus("CANCELLED");
        appointment = appointmentRepository.save(appointment);

        return buildResponse(appointment, "Appointment cancelled successfully");
    }

    // ── HU35 – Reprogramar cita ──────────────────────────────────────────────
    @Transactional
    public AppointmentResponseDTO reprogramarCita(Long appointmentId,
                                                  RescheduleAppointmentRequestDTO request,
                                                  String callerEmail) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + appointmentId));

        // Ownership check
        checkOwnership(appointment, callerEmail);

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new BadRequestException("No se puede reprogramar una cita cancelada");
        }
        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new BadRequestException("No se puede reprogramar una cita completada");
        }

        // Date must not be in the past
        if (request.getNewDate() != null && !request.getNewDate().isAfter(LocalDate.now().minusDays(1))) {
            throw new BadRequestException("La nueva fecha no puede ser en el pasado");
        }
        if (request.getNewStartTime() != null && request.getNewEndTime() != null
                && !request.getNewStartTime().isBefore(request.getNewEndTime())) {
            throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin");
        }

        boolean existeConflicto = appointmentRepository.existsOverlappingAppointmentExcluding(
                appointment.getProvider().getId(),
                request.getNewDate(),
                request.getNewStartTime(),
                request.getNewEndTime(),
                appointmentId);
        if (existeConflicto) {
            throw new ConflictException("El proveedor ya tiene una cita en ese nuevo horario");
        }

        appointment.setDate(request.getNewDate());
        appointment.setStartTime(request.getNewStartTime());
        appointment.setEndTime(request.getNewEndTime());
        appointment.setStatus("SCHEDULED");
        appointment = appointmentRepository.save(appointment);

        return buildResponse(appointment, "Appointment rescheduled successfully");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void checkOwnership(Appointment appointment, String callerEmail) {
        String patientEmail = appointment.getPatient().getAuthUser().getEmail();
        if (!patientEmail.equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para modificar esta cita");
        }
    }

    private AppointmentResponseDTO buildResponse(Appointment a, String message) {
        AppointmentResponseDTO r = new AppointmentResponseDTO();
        r.setAppointmentId(a.getId());
        r.setMessage(message);
        r.setStatus(a.getStatus());
        r.setDate(a.getDate());
        r.setStartTime(a.getStartTime());
        r.setEndTime(a.getEndTime());
        return r;
    }
}