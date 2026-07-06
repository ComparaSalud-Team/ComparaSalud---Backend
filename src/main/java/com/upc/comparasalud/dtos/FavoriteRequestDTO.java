package com.upc.comparasalud.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequestDTO {

    @NotNull(message = "El patientId es obligatorio")
    private Long patientId;

    @NotNull(message = "El providerId es obligatorio")
    private Long providerId;
}