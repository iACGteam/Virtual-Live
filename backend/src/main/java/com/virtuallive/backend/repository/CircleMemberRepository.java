package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.CircleMember;
import com.virtuallive.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CircleMemberRepository extends JpaRepository<CircleMember, Integer> {
    
    // 查询成员关系
    Optional<CircleMember> findByCircleAndUserAndIsActiveTrue(Circle circle, User user);
    
    // 检查是否是成员
    boolean existsByCircleAndUserAndIsActiveTrue(Circle circle, User user);
    
    // 获取圈子成员列表
    Page<CircleMember> findByCircleAndIsActiveTrueOrderByJoinedAtDesc(Circle circle, Pageable pageable);
    
    // 获取用户加入的圈子列表
    Page<CircleMember> findByUserAndIsActiveTrueOrderByJoinedAtDesc(User user, Pageable pageable);
    
    // 统计圈子成员数
    long countByCircleAndIsActiveTrue(Circle circle);
    
    // 统计用户加入的圈子数
    long countByUserAndIsActiveTrue(User user);
}
