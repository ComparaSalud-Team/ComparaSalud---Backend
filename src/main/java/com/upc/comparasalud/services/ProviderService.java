package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.dtos.RegisterProviderRequestDTO;
import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public ProviderDTO registrarProveedor(RegisterProviderRequestDTO dto) {
        AuthUser authUser = new AuthUser();
        authUser.setEmail(dto.getEmail());
        authUser.setPassword(dto.getPassword()); // TODO: Encriptar
        authUser.setRoleId(2); // Provider
        authUser.setIsVerified(false);
        authUser.setCreatedAt(LocalDateTime.now());

        authUser = authUserRepository.save(authUser);

        Provider provider = new Provider();
        provider.setAuthUser(authUser);
        provider.setFullName(dto.getName());
        provider.setPhone(dto.getPhone());
        provider.setSpecialty(dto.getSpecialty());
        provider.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        provider.setRating(BigDecimal.ZERO);
        provider.setIsValidated(false);

        provider = providerRepository.save(provider);

        ProviderDTO providerDTO = modelMapper.map(provider, ProviderDTO.class);
        providerDTO.setEmail(authUser.getEmail());
        providerDTO.setAuthUserId(authUser.getId());

        return providerDTO;
    }

    public List<ProviderDTO> listarTodos() {
        return providerRepository.findAll().stream()
                .map(provider -> {
                    ProviderDTO dto = modelMapper.map(provider, ProviderDTO.class);
                    dto.setEmail(provider.getAuthUser().getEmail());
                    dto.setAuthUserId(provider.getAuthUser().getId());
                    return dto;
                })
                .toList();
    }

    public ProviderDTO buscarPorId(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        ProviderDTO dto = modelMapper.map(provider, ProviderDTO.class);
        dto.setEmail(provider.getAuthUser().getEmail());
        dto.setAuthUserId(provider.getAuthUser().getId());
        return dto;
    }

    public List<ProviderDTO> buscarPorEspecialidad(String specialty) {
        return providerRepository.findBySpecialtyContainingIgnoreCase(specialty).stream()
                .map(provider -> {
                    ProviderDTO dto = modelMapper.map(provider, ProviderDTO.class);
                    dto.setEmail(provider.getAuthUser().getEmail());
                    dto.setAuthUserId(provider.getAuthUser().getId());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public ProviderDTO actualizarProveedor(Long id, ProviderDTO dto) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        provider.setFullName(dto.getFullName());
        provider.setPhone(dto.getPhone());
        provider.setSpecialty(dto.getSpecialty());
        provider.setDescription(dto.getDescription());

        provider = providerRepository.save(provider);

        ProviderDTO response = modelMapper.map(provider, ProviderDTO.class);
        response.setEmail(provider.getAuthUser().getEmail());
        response.setAuthUserId(provider.getAuthUser().getId());
        return response;
    }
}