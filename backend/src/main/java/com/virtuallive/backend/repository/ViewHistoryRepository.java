package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.ViewHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Integer> {
    
    Page<ViewHistory> findByUserUserId(Integer userId, Pageable pageable);
    
    Page<ViewHistory> findByUserUserIdAndViewedAtAfter(Integer userId, LocalDateTime date, Pageable pageable);
    
    // Check if history exists to update time instead of creating new one (optional logic)
    // For now, we just append history or we can find latest.
    
    void deleteByUserUserId(Integer userId);
}
