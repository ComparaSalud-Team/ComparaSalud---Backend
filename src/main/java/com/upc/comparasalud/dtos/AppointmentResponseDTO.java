package com.upc.comparasalud.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

    private Long appointmentId;
    private String message;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}