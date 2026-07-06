package com.upc.comparasalud.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDTO {
    private String date;
    private String startTime;
    private String endTime;
    private Boolean isAvailable;
}