package com.upc.comparasalud.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SecurityActivityDTO {
    private String type;
    private String label;
    private String location;
    private LocalDateTime createdAt;
}
