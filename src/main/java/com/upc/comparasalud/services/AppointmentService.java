package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.AppointmentRequestDTO;
import com.upc.comparasalud.dtos.AppointmentResponseDTO;
import com.upc.comparasalud.dtos.CancelAppointmentRequestDTO;
import com.upc.comparasalud.dtos.RescheduleAppointmentRequestDTO;
import com.upc.comparasalud.entidades.Appointment;
import com.upc.comparasalud.entidades.Patient;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.repositorios.AppointmentRepository;
import com.upc.comparasalud.repositorios.PatientRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Transactional
    public AppointmentResponseDTO agendarCita(AppointmentRequestDTO request) {


        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + request.getPatientId()));

        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + request.getProviderId()));


        boolean existeConflicto = appointmentRepository.existsOverlappingAppointment(
                request.getProviderId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (existeConflicto) {
            throw new RuntimeException("El proveedor ya tiene una cita programada en ese horario");
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

        // 4. Retornar respuesta
        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setAppointmentId(appointment.getId());
        response.setMessage("Cita agendada correctamente");
        response.setStatus(appointment.getStatus());
        response.setDate(appointment.getDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());

        return response;
    }
    @Transactional
    public AppointmentResponseDTO cancelarCita(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + appointmentId));

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException("No se puede cancelar una cita que ya fue completada");
        }

        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setAppointmentId(appointment.getId());
        response.setMessage("Cita cancelada y eliminada correctamente");
        response.setStatus("CANCELLED");
        response.setDate(appointment.getDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());

        appointmentRepository.delete(appointment);

        return response;
    }

    @Transactional
    public AppointmentResponseDTO reprogramarCita(Long appointmentId, RescheduleAppointmentRequestDTO request) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + appointmentId));

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException("No se puede reprogramar una cita cancelada");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException("No se puede reprogramar una cita que ya fue completada");
        }

        // Verificar conflicto de horario, excluyendo la cita actual
        boolean existeConflicto = appointmentRepository.existsOverlappingAppointmentExcluding(
                appointment.getProvider().getId(),
                request.getNewDate(),
                request.getNewStartTime(),
                request.getNewEndTime(),
                appointmentId
        );

        if (existeConflicto) {
            throw new RuntimeException("El proveedor ya tiene una cita programada en ese nuevo horario");
        }

        appointment.setDate(request.getNewDate());
        appointment.setStartTime(request.getNewStartTime());
        appointment.setEndTime(request.getNewEndTime());
        appointment.setStatus("RESCHEDULED");

        appointment = appointmentRepository.save(appointment);

        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setAppointmentId(appointment.getId());
        response.setMessage("Cita reprogramada correctamente");
        response.setStatus(appointment.getStatus());
        response.setDate(appointment.getDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());

        return response;
    }
}