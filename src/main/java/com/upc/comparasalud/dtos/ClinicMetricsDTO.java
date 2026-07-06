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
public class ClinicMetricsDTO {
    private Long dailyAppointments;
    private BigDecimal dailyAppointmentsDeltaPct;
    private BigDecimal monthlyEarnings;
    private BigDecimal earningsDeltaPct;
    private Long newPatientsThisMonth;
    private BigDecimal newPatientsDeltaPct;
    private BigDecimal averageRating;
    private Integer reviewsCount;
}