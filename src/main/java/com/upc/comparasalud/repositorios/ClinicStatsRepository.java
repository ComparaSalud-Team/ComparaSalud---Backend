package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.ClinicStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicStatsRepository extends JpaRepository<ClinicStats, Long> {
    Optional<ClinicStats> findByClinic_Id(Long clinicId);
}