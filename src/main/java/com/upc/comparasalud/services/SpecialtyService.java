package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.SpecialtyDTO;
import com.upc.comparasalud.entidades.Specialty;
import com.upc.comparasalud.repositorios.SpecialtyRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public SpecialtyDTO insertar(SpecialtyDTO dto) {
        Specialty specialty = modelMapper.map(dto, Specialty.class);
        specialty = specialtyRepository.save(specialty);
        return modelMapper.map(specialty, SpecialtyDTO.class);
    }

    public List<SpecialtyDTO> listarTodas() {
        return specialtyRepository.findAll().stream()
                .map(specialty -> modelMapper.map(specialty, SpecialtyDTO.class))
                .toList();
    }

    public List<SpecialtyDTO> listarActivas() {
        return specialtyRepository.findByIsActiveTrue().stream()
                .map(specialty -> modelMapper.map(specialty, SpecialtyDTO.class))
                .toList();
    }

    public SpecialtyDTO buscarPorId(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + id));
        return modelMapper.map(specialty, SpecialtyDTO.class);
    }

    @Transactional
    public void eliminar(Long id) {
        specialtyRepository.deleteById(id);
    }
}