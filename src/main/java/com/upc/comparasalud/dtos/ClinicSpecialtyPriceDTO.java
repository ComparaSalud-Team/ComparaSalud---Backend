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
public class ClinicSpecialtyPriceDTO {
    private Long id;
    private Long clinicId;
    private String clinicName;
    private String district;
    private String specialty;
    private BigDecimal price;
    private Integer durationMinutes;
    private List<String> includes;
}
