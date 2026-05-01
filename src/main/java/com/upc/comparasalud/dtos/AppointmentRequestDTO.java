package com.upc.comparasalud.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

    private Long patientId;
    private Long providerId;
    private String serviceName;        // Ej: "Consulta General"
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
}