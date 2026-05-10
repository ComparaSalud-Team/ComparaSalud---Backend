package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.AppointmentRequestDTO;
import com.upc.comparasalud.dtos.AppointmentResponseDTO;
import com.upc.comparasalud.dtos.RescheduleAppointmentRequestDTO;
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
}