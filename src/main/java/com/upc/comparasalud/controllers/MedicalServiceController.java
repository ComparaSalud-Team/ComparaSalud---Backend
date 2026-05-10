package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.MedicalServiceDTO;
import com.upc.comparasalud.dtos.ServiceCategoryDTO;
import com.upc.comparasalud.services.MedicalServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class MedicalServiceController {

    @Autowired
    private MedicalServiceService medicalServiceService;

    // HU54 – Listar todas las categorías
    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategoryDTO>> listarCategorias() {
        return ResponseEntity.ok(medicalServiceService.listarCategorias());
    }

    // HU54 – Servicios por categoría
    @GetMapping
    public ResponseEntity<List<MedicalServiceDTO>> listarPorCategoria(
            @RequestParam Long categoryId) {
        return ResponseEntity.ok(medicalServiceService.listarPorCategoria(categoryId));
    }

    // Admin – Crear categoría
    @PostMapping("/categories")
    public ResponseEntity<ServiceCategoryDTO> crearCategoria(@RequestBody ServiceCategoryDTO dto) {
        return ResponseEntity.ok(medicalServiceService.crearCategoria(dto));
    }

    // Admin – Crear servicio
    @PostMapping
    public ResponseEntity<MedicalServiceDTO> crearServicio(@RequestBody MedicalServiceDTO dto) {
        return ResponseEntity.ok(medicalServiceService.crearServicio(dto));
    }
}