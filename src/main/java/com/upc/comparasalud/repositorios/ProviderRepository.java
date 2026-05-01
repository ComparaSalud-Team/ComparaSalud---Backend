package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findBySpecialtyContainingIgnoreCase(String specialty);
    Optional<Provider> findByAuthUser(AuthUser authUser);
}