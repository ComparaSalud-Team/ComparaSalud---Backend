package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // HU25 - Ver listado de proveedores
    @GetMapping("/providers")
    public List<ProviderDTO> listarTodos() {
        return providerService.listarTodos();
    }

    // HU26 - Ver detalle de proveedor
    @GetMapping("/providers/{id}")
    public ProviderDTO buscarPorId(@PathVariable Long id) {
        return providerService.buscarPorId(id);
    }

    // HU15 - Buscar por especialidad
    @GetMapping("/providers/specialty/{specialty}")
    public List<ProviderDTO> buscarPorEspecialidad(@PathVariable String specialty) {
        return providerService.buscarPorEspecialidad(specialty);
    }

    // HU13 - Editar perfil del proveedor
    @PutMapping("/providers/{id}")
    public ProviderDTO actualizar(@PathVariable Long id, @RequestBody ProviderDTO providerDTO) {
        return providerService.actualizarProveedor(id, providerDTO);
    }
}