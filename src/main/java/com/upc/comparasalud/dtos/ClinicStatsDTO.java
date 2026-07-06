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
public class ClinicStatsDTO {
    private Long clinicId;
    private BigDecimal bedOccupancyPct;
    private BigDecimal surgeriesCompletedPct;
    private BigDecimal staffAvailablePct;
    private BigDecimal satisfactionPct;
}