package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.provider.id = :providerId " +
            "ORDER BY r.createdAt DESC")
    List<Review> findByProviderIdOrderByCreatedAtDesc(@Param("providerId") Long providerId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.provider.id = :providerId")
    long countByProviderId(@Param("providerId") Long providerId);

    boolean existsByAppointmentId(Long appointmentId);


    @Query("SELECT r FROM Review r JOIN r.provider p JOIN p.clinics c " +
            "WHERE c.id = :clinicId " +
            "ORDER BY r.createdAt DESC")
    List<Review> findByClinicIdOrderByCreatedAtDesc(@Param("clinicId") Long clinicId);
}