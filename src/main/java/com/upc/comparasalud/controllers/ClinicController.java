package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.*;
import com.upc.comparasalud.services.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    @Autowired
    private ClinicService clinicService;

    // Registro de clínica (apartado aparte, previo al registro de proveedores)
    @PostMapping
    public ResponseEntity<ClinicDTO> crear(@RequestBody ClinicDTO dto) {
        return ResponseEntity.ok(clinicService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ClinicDTO>> listarTodas() {
        return ResponseEntity.ok(clinicService.listarTodas());
    }

    // El paciente solo debe ver clínicas activas
    @GetMapping("/active")
    public ResponseEntity<List<ClinicDTO>> listarActivas() {
        return ResponseEntity.ok(clinicService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicDTO> actualizar(@PathVariable Long id, @RequestBody ClinicDTO dto) {
        return ResponseEntity.ok(clinicService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clinicService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // El paciente ve qué médicos atienden en esta clínica
    @GetMapping("/{id}/providers")
    public ResponseEntity<List<ProviderDTO>> verProveedores(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.proveedoresDeClinica(id));
    }

    // El paciente ve los servicios de esta clínica
    @GetMapping("/{id}/services")
    public ResponseEntity<List<MedicalServiceDTO>> verServicios(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.serviciosDeClinica(id));
    }

    // Comparador de proveedores – precios por especialidad de una clínica puntual
    @GetMapping("/{id}/pricing")
    public ResponseEntity<List<ClinicSpecialtyPriceDTO>> verPrecios(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.preciosDeClinica(id));
    }

    @PostMapping("/{id}/pricing")
    public ResponseEntity<ClinicSpecialtyPriceDTO> crearPrecio(
            @PathVariable Long id,
            @RequestBody ClinicSpecialtyPriceDTO dto) {
        return ResponseEntity.ok(clinicService.crearPrecio(id, dto));
    }

    @GetMapping("/pricing")
    public ResponseEntity<List<ClinicSpecialtyPriceDTO>> compararPrecios() {
        return ResponseEntity.ok(clinicService.compararPrecios());
    }


    @GetMapping("/{id}/departments")
    public ResponseEntity<List<DepartmentDTO>> verDepartamentos(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.departamentosDeClinica(id));
    }

    @PostMapping("/{id}/departments")
    public ResponseEntity<DepartmentDTO> crearDepartamento(
            @PathVariable Long id,
            @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(clinicService.crearDepartamento(id, dto));
    }

    @PutMapping("/departments/{departmentId}")
    public ResponseEntity<DepartmentDTO> actualizarDepartamento(
            @PathVariable Long departmentId,
            @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(clinicService.actualizarDepartamento(departmentId, dto));
    }

    @DeleteMapping("/departments/{departmentId}")
    public ResponseEntity<Void> eliminarDepartamento(@PathVariable Long departmentId) {
        clinicService.eliminarDepartamento(departmentId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/stats")
    public ResponseEntity<ClinicStatsDTO> verEstadisticas(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.estadisticasDeClinica(id));
    }

    @PutMapping("/{id}/stats")
    public ResponseEntity<ClinicStatsDTO> actualizarEstadisticas(
            @PathVariable Long id,
            @RequestBody ClinicStatsDTO dto) {
        return ResponseEntity.ok(clinicService.actualizarEstadisticas(id, dto));
    }
    @GetMapping("/me")
    public ResponseEntity<ClinicDTO> obtenerMiClinica(java.security.Principal principal) {
        return ResponseEntity.ok(clinicService.obtenerPorEmail(principal.getName()));
    }

    // Dashboard de la clínica
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<ClinicDashboardDTO> verDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.dashboardDeClinica(id));
    }
}