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
public class ProviderDTO {
    private Long id;
    private Long authUserId;
    private String fullName;
    private String phone;
    private String email;           
    private String specialty;
    private String description;
    private BigDecimal rating;
    private Boolean isValidated;
}
