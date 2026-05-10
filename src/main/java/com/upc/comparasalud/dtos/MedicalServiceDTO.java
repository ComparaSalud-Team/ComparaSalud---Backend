package com.upc.comparasalud.dtos;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MedicalServiceDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isActive;
    private Long categoryId;
    private String categoryName;
}