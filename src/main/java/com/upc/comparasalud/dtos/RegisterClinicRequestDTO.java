package com.upc.comparasalud.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterClinicRequestDTO {
    private String email;
    private String password;
    private String name;
    private String ruc;
    private String phone;
    private String description;
    private String street;
    private String district;
    private String city;
    private String country;
}