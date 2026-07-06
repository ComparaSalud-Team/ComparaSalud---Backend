package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicUpcomingAppointmentDTO {
    private String patientName;
    private String time;
    private String doctorName;
    private String specialty;
    private String status;
}