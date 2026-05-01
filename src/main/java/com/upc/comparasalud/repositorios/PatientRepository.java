package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByAuthUser(AuthUser authUser);
}