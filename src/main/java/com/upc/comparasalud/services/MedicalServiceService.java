package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.MedicalServiceDTO;
import com.upc.comparasalud.dtos.ServiceCategoryDTO;
import com.upc.comparasalud.entidades.MedicalService;
import com.upc.comparasalud.entidades.ServiceCategory;
import com.upc.comparasalud.exceptions.BadRequestException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.MedicalServiceRepository;
import com.upc.comparasalud.repositorios.ServiceCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalServiceService {

    @Autowired private MedicalServiceRepository serviceRepository;
    @Autowired private ServiceCategoryRepository categoryRepository;

    // ── HU54 – Listar categorías ─────────────────────────────────────────────
    public List<ServiceCategoryDTO> listarCategorias() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::toCategoryDTO)
                .toList();
    }

    // ── HU54 – Servicios por categoría ──────────────────────────────────────
    public List<MedicalServiceDTO> listarPorCategoria(Long categoryId) {
        // Validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
        }
        List<MedicalServiceDTO> result = serviceRepository
                .findByCategoryIdAndIsActiveTrue(categoryId).stream()
                .map(this::toServiceDTO)
                .toList();

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron servicios para esta categoría");
        }
        return result;
    }

    // ── Admin helpers ─────────────────────────────────────────────────────────
    @Transactional
    public ServiceCategoryDTO crearCategoria(ServiceCategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }
        ServiceCategory cat = new ServiceCategory();
        cat.setName(dto.getName());
        cat.setDescription(dto.getDescription());
        cat.setIsActive(true);
        cat = categoryRepository.save(cat);
        return toCategoryDTO(cat);
    }

    @Transactional
    public MedicalServiceDTO crearServicio(MedicalServiceDTO dto) {
        ServiceCategory cat = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        MedicalService svc = new MedicalService();
        svc.setName(dto.getName());
        svc.setDescription(dto.getDescription());
        svc.setPrice(dto.getPrice());
        svc.setCategory(cat);
        svc.setIsActive(true);
        svc = serviceRepository.save(svc);
        return toServiceDTO(svc);
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private ServiceCategoryDTO toCategoryDTO(ServiceCategory c) {
        return new ServiceCategoryDTO(c.getId(), c.getName(), c.getDescription(), c.getIsActive());
    }

    private MedicalServiceDTO toServiceDTO(MedicalService s) {
        return new MedicalServiceDTO(
                s.getId(), s.getName(), s.getDescription(), s.getPrice(), s.getIsActive(),
                s.getCategory() != null ? s.getCategory().getId() : null,
                s.getCategory() != null ? s.getCategory().getName() : null
        );
    }
}