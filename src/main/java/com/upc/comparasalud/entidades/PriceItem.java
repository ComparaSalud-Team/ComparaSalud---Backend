package com.upc.comparasalud.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceItem {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "monto")
    private BigDecimal monto;
}