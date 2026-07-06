package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {
    private Long id;
    private Long serviceId;
    private String serviceName;
    private String description;
    private String imageUrl;

    // Precio real del servicio (catalog_services.price), sin descontar.
    private BigDecimal originalPrice;
    private BigDecimal discountPercent;

    // originalPrice * (1 - discountPercent / 100)
    private BigDecimal discountedPrice;

    private Boolean isActive;
    private LocalDate expiresAt;
}
