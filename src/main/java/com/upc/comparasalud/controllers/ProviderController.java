package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.AvailabilityDTO;
import com.upc.comparasalud.dtos.MedicalServiceDTO;
import com.upc.comparasalud.dtos.ProviderSearchResponseDTO;
import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.dtos.ProviderDashboardDTO;
import com.upc.comparasalud.services.MedicalServiceService;
import com.upc.comparasalud.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProviderController {
    @Autowired
    private MedicalServiceService medicalServiceService;

    @Autowired
    private ProviderService providerService;

    // HU25 – Ver listado de proveedores
    @GetMapping("/providers")
    public ResponseEntity<?> listarOBuscar(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(providerService.buscarPorNombre(search, page, size));
        }
        return ResponseEntity.ok(providerService.listarTodos());
    }

    // HU26 – Ver detalle de proveedor
    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.buscarPorId(id));
    }

    // HU15 – Buscar por especialidad
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

    // HU nueva – Buscar por dirección
    @GetMapping("/providers/address")
    public ResponseEntity<List<ProviderDTO>> buscarPorDireccion(@RequestParam String address) {
        return ResponseEntity.ok(providerService.buscarPorDireccion(address));
    }

    // HU13 – Editar perfil del proveedor
    @PutMapping("/providers/{id}")
    public ResponseEntity<ProviderDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ProviderDTO providerDTO,
            Principal principal) {
        return ResponseEntity.ok(providerService.actualizarProveedor(id, providerDTO, principal.getName()));
    }

    // HU14 – Dashboard del proveedor
    @GetMapping("/providers/me/dashboard")
    public ResponseEntity<ProviderDashboardDTO> getDashboard(Principal principal) {
        return ResponseEntity.ok(providerService.getDashboard(principal.getName()));
    }
    // HU18 – Filtrar por precio
    @GetMapping("/providers/filter/price")
    public ResponseEntity<List<ProviderDTO>> filtrarPorPrecio(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(providerService.filtrarPorPrecio(minPrice, maxPrice));
    }
    // HU19 – Filtrar por rating
    @GetMapping("/providers/filter/rating")
    public ResponseEntity<List<ProviderDTO>> filtrarPorRating(
            @RequestParam BigDecimal minRating) {
        return ResponseEntity.ok(providerService.filtrarPorRating(minRating));
    }
    // HU20 – Ordenar resultados
    @GetMapping("/providers/sort")
    public ResponseEntity<List<ProviderDTO>> ordenarResultados(
            @RequestParam String sortBy,
            @RequestParam String order) {
        return ResponseEntity.ok(providerService.ordenarResultados(sortBy, order));
    }

    // HU – Filtrar por disponibilidad (hoy, esta-semana, este-mes)
    @GetMapping("/providers/filter/availability")
    public ResponseEntity<List<ProviderDTO>> filtrarPorDisponibilidad(
            @RequestParam String period) {
        return ResponseEntity.ok(providerService.filtrarPorDisponibilidad(period));
    }

    // HU27 – Ver servicios de un proveedor
    // Devuelve los servicios que ESE proveedor tiene seleccionados
    // (provider.getServices()), no el catálogo general.
    @GetMapping("/providers/{id}/services")
    public ResponseEntity<?> verServiciosPorProveedor(@PathVariable Long id) {
        ProviderDTO provider = providerService.buscarPorId(id);
        if (provider.getServices() == null || provider.getServices().isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Este proveedor no tiene servicios registrados"));
        }
        return ResponseEntity.ok(provider.getServices());
    }

    // HU29 – Ver disponibilidad del proveedor
    @GetMapping("/providers/{id}/availability")
    public ResponseEntity<List<AvailabilityDTO>> verDisponibilidad(
            @PathVariable Long id,
            @RequestParam String date) {
        return ResponseEntity.ok(providerService.verDisponibilidad(id, date));
    }

    // Actualizar los servicios seleccionados por el proveedor
    @PutMapping("/providers/{id}/services")
    public ResponseEntity<ProviderDTO> actualizarServicios(
            @PathVariable Long id,
            @RequestBody List<Long> serviceIds,
            Principal principal) {
        return ResponseEntity.ok(providerService.actualizarServicios(id, serviceIds, principal.getName()));
    }
}