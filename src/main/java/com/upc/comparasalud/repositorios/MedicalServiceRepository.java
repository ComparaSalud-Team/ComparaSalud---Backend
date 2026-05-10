package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    List<MedicalService> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<MedicalService> findByIsActiveTrue();
}