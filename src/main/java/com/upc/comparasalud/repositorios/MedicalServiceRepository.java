package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    List<MedicalService> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<MedicalService> findByIsActiveTrue();
    
    @Query("SELECT s FROM MedicalService s " +
            "WHERE s.isActive = true " +
            "AND s.category.id IN (" +
            "SELECT p.id FROM Provider p WHERE p.id = :providerId)")
    List<MedicalService> findActiveByProviderId(@Param("providerId") Long providerId);

}