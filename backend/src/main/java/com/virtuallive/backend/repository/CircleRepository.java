package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Circle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircleRepository extends JpaRepository<Circle, Integer> {
    
    // 查找活跃的圈子,按创建时间倒序
    Page<Circle> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // 查找活跃的圈子,按成员数倒序(热门)
    @Query("SELECT c FROM Circle c WHERE c.isActive = true ORDER BY c.memberCount DESC, c.postCount DESC")
    Page<Circle> findPopularCircles(Pageable pageable);
    
    // 按名称或描述搜索圈子
    @Query("SELECT c FROM Circle c WHERE c.isActive = true AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)")
    Page<Circle> searchCircles(@Param("keyword") String keyword, Pageable pageable);
    
    // 按分类查找圈子
    Page<Circle> findByCategoryAndIsActiveTrue(String category, Pageable pageable);
    
    // 查找官方圈子
    Page<Circle> findByIsOfficialTrueAndIsActiveTrueOrderByMemberCountDesc(Pageable pageable);
}
