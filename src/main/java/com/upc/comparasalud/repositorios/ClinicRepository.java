package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    boolean existsByName(String name);
    boolean existsByRuc(String ruc);
    List<Clinic> findByIsActiveTrue();
    Optional<Clinic> findByAuthUser_Email(String email);
}