package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.OfferDTO;
import com.upc.comparasalud.entidades.MedicalService;
import com.upc.comparasalud.entidades.Offer;
import com.upc.comparasalud.repositorios.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class OfferService {

    @Autowired private OfferRepository offerRepository;

    // "Ofertas exclusivas para ti" del dashboard – ofertas activas y no vencidas.
    public List<OfferDTO> listarActivas() {
        LocalDate hoy = LocalDate.now();
        return offerRepository.findByIsActiveTrue().stream()
                .filter(o -> o.getExpiresAt() == null || !o.getExpiresAt().isBefore(hoy))
                .map(this::toDTO)
                .toList();
    }

    private OfferDTO toDTO(Offer o) {
        MedicalService service = o.getService();
        BigDecimal factor = BigDecimal.ONE.subtract(
                o.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

        OfferDTO dto = new OfferDTO();
        dto.setId(o.getId());
        dto.setServiceId(service.getId());
        dto.setServiceName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setImageUrl(o.getImageUrl());
        dto.setOriginalPrice(service.getPrice());
        dto.setDiscountPercent(o.getDiscountPercent());
        dto.setDiscountedPrice(service.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        dto.setIsActive(o.getIsActive());
        dto.setExpiresAt(o.getExpiresAt());
        return dto;
    }
}
