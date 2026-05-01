package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.AppointmentRequestDTO;
import com.upc.comparasalud.dtos.AppointmentResponseDTO;
import com.upc.comparasalud.dtos.CancelAppointmentRequestDTO;
import com.upc.comparasalud.dtos.RescheduleAppointmentRequestDTO;
import com.upc.comparasalud.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // HU33 – Agendar cita
    @PostMapping("/appointments")
    public AppointmentResponseDTO agendarCita(@RequestBody AppointmentRequestDTO request) {
        return appointmentService.agendarCita(request);
    }
    // HU34 – Cancelar cita
    @DeleteMapping("/appointments/{id}/cancel")
    public AppointmentResponseDTO cancelarCita(@PathVariable Long id) {
        return appointmentService.cancelarCita(id);
    }

    // HU35 – Reprogramar cita
    @PatchMapping("/appointments/{id}/reschedule")
    public AppointmentResponseDTO reprogramarCita(
            @PathVariable Long id,
            @RequestBody RescheduleAppointmentRequestDTO request) {
        return appointmentService.reprogramarCita(id, request);
    }
}