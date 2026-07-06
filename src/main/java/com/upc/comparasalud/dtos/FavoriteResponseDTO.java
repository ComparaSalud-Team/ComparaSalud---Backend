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
public class FavoriteResponseDTO {
    private Long favoriteId;
    private Long providerId;
    private String fullName;
    private String specialty;
    private BigDecimal pricePerAppointment;
    private BigDecimal averageRating;
    private Integer experienceYears;
    private String district;
    private String city;
    private List<Long> clinicIds;
    private List<String> clinicNames;

    private String photoUrl;
}