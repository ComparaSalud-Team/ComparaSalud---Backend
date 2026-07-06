package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByPatientIdAndProviderId(Long patientId, Long providerId);

    Optional<Favorite> findByPatientIdAndProviderId(Long patientId, Long providerId);

    List<Favorite> findByPatientId(Long patientId);
}