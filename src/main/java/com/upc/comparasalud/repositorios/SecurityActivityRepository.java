package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.SecurityActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SecurityActivityRepository extends JpaRepository<SecurityActivity, Long> {

    List<SecurityActivity> findByAuthUserIdOrderByCreatedAtDesc(Long authUserId, Pageable pageable);
}
