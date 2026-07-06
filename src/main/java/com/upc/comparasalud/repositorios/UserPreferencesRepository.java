package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByAuthUserId(Long authUserId);
}
