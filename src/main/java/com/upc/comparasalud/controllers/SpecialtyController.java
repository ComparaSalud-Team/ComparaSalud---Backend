package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.SpecialtyDTO;
import com.upc.comparasalud.services.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SpecialtyController {

    @Autowired
    private SpecialtyService specialtyService;

    @PostMapping("/specialties")
    public SpecialtyDTO insertar(@RequestBody SpecialtyDTO dto) {
        return specialtyService.insertar(dto);
    }

    @GetMapping("/specialties")
    public List<SpecialtyDTO> listarTodas() {
        return specialtyService.listarTodas();
    }

    @GetMapping("/specialties/active")
    public List<SpecialtyDTO> listarActivas() {
        return specialtyService.listarActivas();
    }

    @GetMapping("/specialties/{id}")
    public SpecialtyDTO buscarPorId(@PathVariable Long id) {
        return specialtyService.buscarPorId(id);
    }

    @DeleteMapping("/specialties/{id}")
    public void eliminar(@PathVariable Long id) {
        specialtyService.eliminar(id);
    }
}
