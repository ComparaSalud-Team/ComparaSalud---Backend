package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.ClinicSpecialtyPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicSpecialtyPriceRepository extends JpaRepository<ClinicSpecialtyPrice, Long> {
    List<ClinicSpecialtyPrice> findByClinic_Id(Long clinicId);
    List<ClinicSpecialtyPrice> findBySpecialtyIgnoreCase(String specialty);
    List<ClinicSpecialtyPrice> findByClinic_IsActiveTrue();
    Optional<ClinicSpecialtyPrice> findByClinic_IdAndSpecialtyIgnoreCase(Long clinicId, String specialty);
}
