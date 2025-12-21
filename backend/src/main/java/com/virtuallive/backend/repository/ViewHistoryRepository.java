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
    
    @org.springframework.data.jpa.repository.Query("SELECT vh FROM ViewHistory vh JOIN FETCH vh.video v JOIN FETCH v.author WHERE vh.user.userId = :userId AND vh.viewedAt >= :date AND (v.tags IS NULL OR v.tags NOT LIKE '%__PRIVATE__%')")
    Page<ViewHistory> findPublicHistoryByUserAndDate(@org.springframework.data.repository.query.Param("userId") Integer userId, @org.springframework.data.repository.query.Param("date") LocalDateTime date, Pageable pageable);

    // Check if history exists to update time instead of creating new one (optional logic)
    // For now, we just append history or we can find latest.
    
    void deleteByUserUserId(Integer userId);
}
