package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.CircleMember;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.CircleMemberRepository;
import com.virtuallive.backend.repository.CircleRepository;
import com.virtuallive.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CircleMemberService {
    
    private final CircleMemberRepository circleMemberRepository;
    private final CircleRepository circleRepository;
    private final UserRepository userRepository;
    
    /**
     * 加入/退出圈子
     */
    @Transactional
    public boolean toggleMembership(Integer userId, Integer circleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        Optional<CircleMember> existingMember = circleMemberRepository
                .findByCircleAndUserAndIsActiveTrue(circle, user);
        
        if (existingMember.isPresent()) {
            // 已加入，退出圈子
            CircleMember member = existingMember.get();
            member.setIsActive(false);
            circleMemberRepository.save(member);
            
            // 更新圈子成员数
            circle.setMemberCount(Math.max(0, circle.getMemberCount() - 1));
            circleRepository.save(circle);
            
            log.info("用户 {} 退出圈子 {}", userId, circleId);
            return false;
        } else {
            // 未加入，加入圈子
            CircleMember member = CircleMember.builder()
                    .circle(circle)
                    .user(user)
                    .build();
            circleMemberRepository.save(member);
            
            // 更新圈子成员数
            circle.setMemberCount(circle.getMemberCount() + 1);
            circleRepository.save(circle);
            
            log.info("用户 {} 加入圈子 {}", userId, circleId);
            return true;
        }
    }
    
    /**
     * 检查是否是圈子成员
     */
    public boolean isMember(Integer userId, Integer circleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        return circleMemberRepository.existsByCircleAndUserAndIsActiveTrue(circle, user);
    }
    
    /**
     * 获取圈子成员列表
     */
    public Page<CircleMember> getCircleMembers(Integer circleId, int page, int size) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return circleMemberRepository.findByCircleAndIsActiveTrueOrderByJoinedAtDesc(circle, pageable);
    }
    
    /**
     * 获取用户加入的圈子列表
     */
    public Page<CircleMember> getUserCircles(Integer userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return circleMemberRepository.findByUserAndIsActiveTrueOrderByJoinedAtDesc(user, pageable);
    }
    
    /**
     * 获取圈子成员数
     */
    public long getMemberCount(Integer circleId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        return circleMemberRepository.countByCircleAndIsActiveTrue(circle);
    }
}
