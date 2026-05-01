package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);
}