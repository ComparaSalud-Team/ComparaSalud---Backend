package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.FavoriteRequestDTO;
import com.upc.comparasalud.dtos.FavoriteResponseDTO;
import com.upc.comparasalud.entidades.Favorite;
import com.upc.comparasalud.entidades.Patient;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.exceptions.ConflictException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.FavoriteRepository;
import com.upc.comparasalud.repositorios.PatientRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ProviderRepository providerRepository;

    @Transactional
    public Map<String, String> agregarFavorito(FavoriteRequestDTO request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado con ID: " + request.getPatientId()));

        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con ID: " + request.getProviderId()));

        // Criterio 2 – Evitar duplicidad
        if (favoriteRepository.existsByPatientIdAndProviderId(
                request.getPatientId(), request.getProviderId())) {
            throw new ConflictException("El proveedor ya está en tu lista de favoritos");
        }

        Favorite favorite = new Favorite();
        favorite.setPatient(patient);
        favorite.setProvider(provider);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);

        return Map.of("message", "Provider added to favorites");
    }

    // Criterio 4 – Eliminar favorito
    @Transactional
    public Map<String, String> eliminarFavorito(Long patientId, Long providerId) {

        Favorite favorite = favoriteRepository
                .findByPatientIdAndProviderId(patientId, providerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El proveedor no está en tu lista de favoritos"));

        favoriteRepository.delete(favorite);
        return Map.of("message", "Provider removed from favorites");
    }

    // Criterio 3 y 5 – Listar favoritos del paciente
    public List<FavoriteResponseDTO> listarFavoritos(Long patientId) {

        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado con ID: " + patientId));

        List<Favorite> favorites = favoriteRepository.findByPatientId(patientId);

        return favorites.stream().map(f -> {
            Provider p = f.getProvider();
            return new FavoriteResponseDTO(
                    f.getId(),
                    p.getId(),
                    p.getFullName(),
                    p.getSpecialty(),
                    p.getPricePerAppointment(),
                    p.getAverageRating(),
                    p.getExperienceYears(),
                    p.getDistrict(),
                    p.getCity(),
                    p.getClinics().stream().map(com.upc.comparasalud.entidades.Clinic::getId).toList(),
                    p.getClinics().stream().map(com.upc.comparasalud.entidades.Clinic::getName).toList(),
                    p.getAuthUser().getProfilePhotoUrl()
            );
        }).toList();
    }
}