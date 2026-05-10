package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // HU25 – Ver listado de proveedores
    @GetMapping("/providers")
    public ResponseEntity<List<ProviderDTO>> listarTodos() {
        return ResponseEntity.ok(providerService.listarTodos());
    }

    // HU26 – Ver detalle de proveedor
    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.buscarPorId(id));
    }

    // HU15 – Buscar por especialidad (returns clear message when empty)
    @GetMapping("/providers/specialty/{specialty}")
    public ResponseEntity<?> buscarPorEspecialidad(@PathVariable String specialty) {
        List<ProviderDTO> result = providerService.buscarPorEspecialidad(specialty);
        if (result.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "No se encontraron proveedores para esta especialidad")
            );
        }
        return ResponseEntity.ok(result);
    }

    // HU13 – Editar perfil del proveedor (ownership enforced)
    @PutMapping("/providers/{id}")
    public ResponseEntity<ProviderDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ProviderDTO providerDTO,
            Principal principal) {
        return ResponseEntity.ok(providerService.actualizarProveedor(id, providerDTO, principal.getName()));
    }
}