package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.DailyCheckin;
import com.virtuallive.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyCheckinRepository extends JpaRepository<DailyCheckin, Integer> {
    
    // 查询用户在某圈子某天的签到记录
    Optional<DailyCheckin> findByUserAndCircleAndCheckinDate(User user, Circle circle, LocalDate checkinDate);
    
    // 检查用户今天是否已签到
    boolean existsByUserAndCircleAndCheckinDate(User user, Circle circle, LocalDate checkinDate);
    
    // 获取用户在圈子的最后一次签到记录
    @Query("SELECT d FROM DailyCheckin d WHERE d.user = ?1 AND d.circle = ?2 ORDER BY d.checkinDate DESC LIMIT 1")
    Optional<DailyCheckin> findLastCheckinByUserAndCircle(User user, Circle circle);
    
    // 统计用户在圈子的总签到天数
    long countByUserAndCircle(User user, Circle circle);
}
