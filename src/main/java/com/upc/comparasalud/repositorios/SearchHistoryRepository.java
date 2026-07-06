package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByAuthUserIdOrderByCreatedAtDesc(Long userId);
    List<SearchHistory> findByAuthUserIdAndSavedTrueOrderByCreatedAtDesc(Long userId);
    Optional<SearchHistory> findByIdAndAuthUserId(Long id, Long userId);
}