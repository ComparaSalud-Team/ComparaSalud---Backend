package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "location")
    private String location;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SecurityActivity(AuthUser authUser, String type, String label, String location) {
        this.authUser = authUser;
        this.type = type;
        this.label = label;
        this.location = location;
        this.createdAt = LocalDateTime.now();
    }
}
