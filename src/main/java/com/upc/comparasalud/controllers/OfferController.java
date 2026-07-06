package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.OfferDTO;
import com.upc.comparasalud.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    // "Ofertas exclusivas para ti" del dashboard
    @GetMapping("/active")
    public ResponseEntity<List<OfferDTO>> listarActivas() {
        return ResponseEntity.ok(offerService.listarActivas());
    }
}
