package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.AppointmentRequestDTO;
import com.upc.comparasalud.dtos.AppointmentResponseDTO;
import com.upc.comparasalud.dtos.RescheduleAppointmentRequestDTO;
import com.upc.comparasalud.dtos.AppointmentHistoryDTO;
import com.upc.comparasalud.exceptions.BadRequestException;
import java.util.List;
import com.upc.comparasalud.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // HU33 – Agendar cita
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponseDTO> agendarCita(
            @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity.ok(appointmentService.agendarCita(request));
    }

    // HU34 – Cancelar cita  (PUT, no DELETE; status → CANCELLED, no se borra)
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelarCita(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(appointmentService.cancelarCita(id, principal.getName()));
    }

    // HU35 – Reprogramar cita  (PUT, status → SCHEDULED)
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<AppointmentResponseDTO> reprogramarCita(
            @PathVariable Long id,
            @RequestBody RescheduleAppointmentRequestDTO request,
            Principal principal) {
        return ResponseEntity.ok(appointmentService.reprogramarCita(id, request, principal.getName()));
    }

    // Próximas citas – soporta tanto paciente (userId) como proveedor (providerId)
    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<AppointmentHistoryDTO>> obtenerProximasCitas(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long providerId) {
        if (providerId != null) {
            return ResponseEntity.ok(appointmentService.obtenerProximasCitasProveedor(providerId));
        }
        if (userId != null) {
            return ResponseEntity.ok(appointmentService.obtenerProximasCitas(userId));
        }
        throw new BadRequestException("Debe especificar userId o providerId");
    }

    // Historial – soporta tanto paciente (userId) como proveedor (providerId)
    @GetMapping("/appointments/history")
    public ResponseEntity<List<AppointmentHistoryDTO>> obtenerHistorial(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long providerId) {
        if (providerId != null) {
            return ResponseEntity.ok(appointmentService.obtenerHistorialProveedor(providerId));
        }
        if (userId != null) {
            return ResponseEntity.ok(appointmentService.obtenerHistorial(userId));
        }
        throw new BadRequestException("Debe especificar userId o providerId");
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentHistoryDTO> obtenerPorId(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(appointmentService.obtenerPorId(id, principal.getName()));
    }

}