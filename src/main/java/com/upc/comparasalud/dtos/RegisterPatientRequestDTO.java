package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPatientRequestDTO {
    private String email;
    private String password;
    private String name;
    private String phone;
    private String birthday;  // opcional, formato "YYYY-MM-DD"
    private String country;   // opcional
}
