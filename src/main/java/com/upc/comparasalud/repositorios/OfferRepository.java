package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByIsActiveTrue();
}
