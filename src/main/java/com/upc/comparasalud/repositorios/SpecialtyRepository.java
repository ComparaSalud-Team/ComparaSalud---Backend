package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    List<Specialty> findByIsActiveTrue();

    boolean existsByName(String name);
}