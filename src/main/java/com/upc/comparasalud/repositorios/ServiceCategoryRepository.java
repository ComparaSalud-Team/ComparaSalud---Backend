package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    List<ServiceCategory> findByIsActiveTrue();
    boolean existsByName(String name);
}