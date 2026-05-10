package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "catalog_service_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
}