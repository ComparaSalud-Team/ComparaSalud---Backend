package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.SearchHistoryDTO;
import com.upc.comparasalud.services.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService searchHistoryService;

    // HU22 – Guardar búsqueda
    @PostMapping("/search-history")
    public ResponseEntity<Map<String, String>> guardarBusqueda(
            @RequestBody SearchHistoryDTO dto,
            Principal principal) {
        String msg = searchHistoryService.guardarBusqueda(dto, principal.getName());
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // HU22 – Ver historial
    @GetMapping("/search-history")
    public ResponseEntity<List<SearchHistoryDTO>> obtenerHistorial(Principal principal) {
        return ResponseEntity.ok(searchHistoryService.obtenerHistorial(principal.getName()));
    }

    // Ver solo las búsquedas guardadas (pestaña "Guardados")
    @GetMapping("/search-history/saved")
    public ResponseEntity<List<SearchHistoryDTO>> obtenerGuardados(Principal principal) {
        return ResponseEntity.ok(searchHistoryService.obtenerGuardados(principal.getName()));
    }

    // Marcar una búsqueda del historial como guardada
    @PutMapping("/search-history/{id}/save")
    public ResponseEntity<Map<String, String>> marcarComoGuardada(
            @PathVariable Long id,
            Principal principal) {
        String msg = searchHistoryService.marcarComoGuardada(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // Quitar una búsqueda de guardados
    @DeleteMapping("/search-history/{id}/save")
    public ResponseEntity<Map<String, String>> quitarDeGuardados(
            @PathVariable Long id,
            Principal principal) {
        String msg = searchHistoryService.quitarDeGuardados(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", msg));
    }
}