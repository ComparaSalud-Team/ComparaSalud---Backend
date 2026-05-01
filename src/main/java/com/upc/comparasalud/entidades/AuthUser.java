package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_users")
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "role_id")
    private Integer roleId;           // 1 = Patient, 2 = Provider, 3 = Admin

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "authUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Patient patient;

    @OneToOne(mappedBy = "authUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Provider provider;
}



