package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private Long authUserId;
    private String name;
    private String phone;
    private String email;        
    private String country;
}
