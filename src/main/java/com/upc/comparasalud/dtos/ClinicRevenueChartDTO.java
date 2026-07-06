package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicRevenueChartDTO {
    private String monthLabel;
    private BigDecimal total;
    private BigDecimal deltaVsPreviousMonthPct;
    private List<ClinicRevenuePointDTO> points;
}