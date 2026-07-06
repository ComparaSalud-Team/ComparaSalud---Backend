package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.SearchHistoryDTO;
import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.SearchHistory;
import com.upc.comparasalud.exceptions.ForbiddenException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchHistoryService {

    @Autowired private SearchHistoryRepository searchHistoryRepository;
    @Autowired private AuthUserRepository authUserRepository;

    // HU22 – Guardar búsqueda
    public String guardarBusqueda(SearchHistoryDTO dto, String callerEmail) {
        AuthUser authUser = authUserRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        SearchHistory history = new SearchHistory();
        history.setAuthUser(authUser);
        history.setKeyword(dto.getKeyword());
        history.setMinPrice(dto.getMinPrice());
        history.setMaxPrice(dto.getMaxPrice());
        history.setRating(dto.getRating());
        history.setSpecialty(dto.getSpecialty());
        history.setDistrict(dto.getDistrict());
        history.setCreatedAt(LocalDateTime.now());
        history.setSaved(false);
        searchHistoryRepository.save(history);

        return "Búsqueda guardada correctamente";
    }

    // HU22 – Ver historial
    public List<SearchHistoryDTO> obtenerHistorial(String callerEmail) {
        AuthUser authUser = authUserRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return searchHistoryRepository
                .findByAuthUserIdOrderByCreatedAtDesc(authUser.getId())
                .stream().map(this::toDTO).toList();
    }

    // Ver solo las búsquedas marcadas como guardadas (pestaña "Guardados")
    public List<SearchHistoryDTO> obtenerGuardados(String callerEmail) {
        AuthUser authUser = authUserRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return searchHistoryRepository
                .findByAuthUserIdAndSavedTrueOrderByCreatedAtDesc(authUser.getId())
                .stream().map(this::toDTO).toList();
    }

    // Marcar una búsqueda del historial como guardada
    public String marcarComoGuardada(Long historyId, String callerEmail) {
        SearchHistory history = obtenerPropia(historyId, callerEmail);
        history.setSaved(true);
        searchHistoryRepository.save(history);
        return "Búsqueda guardada en favoritos";
    }

    // Quitar una búsqueda de guardados (sigue existiendo en el historial)
    public String quitarDeGuardados(Long historyId, String callerEmail) {
        SearchHistory history = obtenerPropia(historyId, callerEmail);
        history.setSaved(false);
        searchHistoryRepository.save(history);
        return "Búsqueda quitada de guardados";
    }

    private SearchHistory obtenerPropia(Long historyId, String callerEmail) {
        AuthUser authUser = authUserRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        SearchHistory history = searchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("Búsqueda no encontrada"));

        if (!history.getAuthUser().getId().equals(authUser.getId())) {
            throw new ForbiddenException("Esta búsqueda no te pertenece");
        }
        return history;
    }

    private SearchHistoryDTO toDTO(SearchHistory h) {
        SearchHistoryDTO dto = new SearchHistoryDTO();
        dto.setId(h.getId());
        dto.setKeyword(h.getKeyword());
        dto.setMinPrice(h.getMinPrice());
        dto.setMaxPrice(h.getMaxPrice());
        dto.setRating(h.getRating());
        dto.setSpecialty(h.getSpecialty());
        dto.setDistrict(h.getDistrict());
        dto.setCreatedAt(h.getCreatedAt());
        dto.setSaved(h.getSaved());
        return dto;
    }
}