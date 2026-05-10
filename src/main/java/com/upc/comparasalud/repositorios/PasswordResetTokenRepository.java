package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByAuthUserId(Long userId);
}