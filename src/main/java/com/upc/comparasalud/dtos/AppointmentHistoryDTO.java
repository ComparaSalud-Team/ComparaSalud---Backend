package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentHistoryDTO {

    private Long appointmentId;
    private String date;
    private String time;
    private String status;
    private String doctor;

    private Long providerId;
    private String specialty;
    private String photoUrl;
    private BigDecimal rating;
    private BigDecimal price;
    private Integer durationMinutes;
    private String modality;
    private String district;

    private Long patientId;
    private String patient;
    private String patientPhotoUrl;
    private String reason;
}