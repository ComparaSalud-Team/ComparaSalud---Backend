package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_providers")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(name = "full_name")
    private String fullName;

    private String phone;
    private String specialty;
    private String description;

    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "is_validated")
    private Boolean isValidated = false;
}
