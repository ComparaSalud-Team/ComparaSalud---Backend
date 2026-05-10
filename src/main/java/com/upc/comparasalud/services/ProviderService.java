package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.exceptions.ForbiddenException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {

    @Autowired private ProviderRepository providerRepository;
    @Autowired private AuthUserRepository authUserRepository;
    @Autowired private ModelMapper modelMapper;

    public List<ProviderDTO> listarTodos() {
        return providerRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ProviderDTO buscarPorId(Long id) {
        return toDTO(providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id)));
    }

    public List<ProviderDTO> buscarPorEspecialidad(String specialty) {
        return providerRepository.findBySpecialtyContainingIgnoreCase(specialty)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ProviderDTO actualizarProveedor(Long id, ProviderDTO dto, String callerEmail) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (!provider.getAuthUser().getEmail().equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para modificar este perfil");
        }

        provider.setFullName(dto.getFullName());
        provider.setPhone(dto.getPhone());
        provider.setSpecialty(dto.getSpecialty());
        provider.setDescription(dto.getDescription());
        provider = providerRepository.save(provider);
        return toDTO(provider);
    }

    private ProviderDTO toDTO(Provider provider) {
        ProviderDTO dto = modelMapper.map(provider, ProviderDTO.class);
        dto.setEmail(provider.getAuthUser().getEmail());
        dto.setAuthUserId(provider.getAuthUser().getId());
        return dto;
    }
}