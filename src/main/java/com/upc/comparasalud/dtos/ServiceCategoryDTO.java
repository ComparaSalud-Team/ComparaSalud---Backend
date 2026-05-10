package com.upc.comparasalud.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ServiceCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}