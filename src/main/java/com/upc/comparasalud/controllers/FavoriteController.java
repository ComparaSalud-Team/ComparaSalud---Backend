package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.FavoriteRequestDTO;
import com.upc.comparasalud.dtos.FavoriteResponseDTO;
import com.upc.comparasalud.services.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Criterio 1 y 2 – Agregar favorito (evita duplicados)
    @PostMapping
    public ResponseEntity<Map<String, String>> agregarFavorito(
            @Valid @RequestBody FavoriteRequestDTO request) {
        return ResponseEntity.ok(favoriteService.agregarFavorito(request));
    }

    // Criterio 4 – Eliminar favorito
    @DeleteMapping
    public ResponseEntity<Map<String, String>> eliminarFavorito(
            @RequestParam Long patientId,
            @RequestParam Long providerId) {
        return ResponseEntity.ok(favoriteService.eliminarFavorito(patientId, providerId));
    }

    // Criterio 3 y 5 – Ver lista de favoritos del paciente
    @GetMapping
    public ResponseEntity<List<FavoriteResponseDTO>> listarFavoritos(
            @RequestParam Long patientId) {
        return ResponseEntity.ok(favoriteService.listarFavoritos(patientId));
    }
}