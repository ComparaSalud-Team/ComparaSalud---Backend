package com.upc.comparasalud.entidades;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ProviderAddress {

    private String street;
    private String district;
    private String city;
    private String country;
}