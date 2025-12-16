package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.RoleCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleCardRepository extends JpaRepository<RoleCard, Integer> {
    
    List<RoleCard> findByUserUserIdOrderBySubmitTimeDesc(Integer userId);
    
    List<RoleCard> findByUserUserIdAndStatus(Integer userId, RoleCard.Status status);
}
