package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clinic_stats")
public class ClinicStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false, unique = true)
    private Clinic clinic;

    @Column(name = "bed_occupancy_pct", precision = 5, scale = 2)
    private BigDecimal bedOccupancyPct = BigDecimal.ZERO;

    @Column(name = "surgeries_completed_pct", precision = 5, scale = 2)
    private BigDecimal surgeriesCompletedPct = BigDecimal.ZERO;

    @Column(name = "staff_available_pct", precision = 5, scale = 2)
    private BigDecimal staffAvailablePct = BigDecimal.ZERO;

    @Column(name = "satisfaction_pct", precision = 5, scale = 2)
    private BigDecimal satisfactionPct = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}