package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.MedicalServiceDTO;
import com.upc.comparasalud.dtos.ServiceCategoryDTO;
import com.upc.comparasalud.services.MedicalServiceService;
import com.upc.comparasalud.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class MedicalServiceController {
    @Autowired
    private ProviderService providerService;

    @Autowired
    private MedicalServiceService medicalServiceService;

    // HU54 – Listar todas las categorías
    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategoryDTO>> listarCategorias() {
        return ResponseEntity.ok(medicalServiceService.listarCategorias());
    }

    // "Servicios más solicitados" del dashboard – catálogo completo de
    // servicios activos, sin necesidad de pasar por un proveedor puntual.
    @GetMapping("/active")
    public ResponseEntity<List<MedicalServiceDTO>> listarActivos() {
        return ResponseEntity.ok(medicalServiceService.listarActivos());
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
    // HU27 – Ver servicios de un proveedor
    @GetMapping("/providers/{id}/services")
    public ResponseEntity<?> verServiciosPorProveedor(@PathVariable Long id) {
        // Verificar que el proveedor existe
        providerService.buscarPorId(id); // lanza 404 si no existe

        List<MedicalServiceDTO> servicios = medicalServiceService.listarActivos();
        if (servicios.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Este proveedor no tiene servicios disponibles"));
        }
        return ResponseEntity.ok(servicios);
    }
}