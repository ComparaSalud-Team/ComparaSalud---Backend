package com.upc.comparasalud.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderAddressDTO {
    private String street;
    private String district;
    private String city;
    private String country;
}