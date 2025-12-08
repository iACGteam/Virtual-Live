package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.CircleDto;
import com.virtuallive.backend.model.dto.CircleMemberDto;
import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.CircleMember;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.CircleMemberRepository;
import com.virtuallive.backend.repository.CircleRepository;
import com.virtuallive.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CircleService {
    
    private final CircleRepository circleRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final UserRepository userRepository;
    
    public Page<CircleDto> getCircles(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Circle> circles;
        if ("hot".equals(sort) || "popular".equals(sort)) {
            circles = circleRepository.findPopularCircles(pageable);
        } else {
            circles = circleRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        }
        
        return circles.map(this::convertToDto);
    }
    
    public Page<CircleDto> searchCircles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.searchCircles(keyword, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CircleDto> getCirclesByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.findByCategoryAndIsActiveTrue(category, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CircleDto> getOfficialCircles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.findByIsOfficialTrueAndIsActiveTrueOrderByMemberCountDesc(pageable)
                .map(this::convertToDto);
    }
    
    public CircleDto getCircleById(Integer id) {
        Circle circle = circleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        return convertToDto(circle);
    }
    
    /**
     * 加入圈子
     */
    @Transactional
    public Map<String, Object> joinCircle(Integer circleId, Integer userId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否已经是成员
        Optional<CircleMember> existingMember = circleMemberRepository
                .findByCircleAndUserAndIsActiveTrue(circle, user);
        
        if (existingMember.isPresent()) {
            throw new RuntimeException("已经是圈子成员");
        }
        
        // 检查是否有被禁用的记录，如果有则重新激活
        Optional<CircleMember> inactiveMember = circleMemberRepository
                .findByCircleAndUserAndIsActiveTrue(circle, user);
        
        CircleMember member;
        if (inactiveMember.isPresent()) {
            member = inactiveMember.get();
            member.setIsActive(true);
        } else {
            member = CircleMember.builder()
                    .circle(circle)
                    .user(user)
                    .postCount(0)
                    .isActive(true)
                    .build();
        }
        
        circleMemberRepository.save(member);
        
        // 更新圈子成员数
        circle.setMemberCount(circle.getMemberCount() + 1);
        circleRepository.save(circle);
        
        return Map.of(
                "isMember", true,
                "memberCount", circle.getMemberCount()
        );
    }
    
    /**
     * 退出圈子
     */
    @Transactional
    public Map<String, Object> leaveCircle(Integer circleId, Integer userId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        CircleMember member = circleMemberRepository
                .findByCircleAndUserAndIsActiveTrue(circle, user)
                .orElseThrow(() -> new RuntimeException("不是圈子成员"));
        
        // 软删除：设置为不活跃
        member.setIsActive(false);
        circleMemberRepository.save(member);
        
        // 更新圈子成员数
        if (circle.getMemberCount() > 0) {
            circle.setMemberCount(circle.getMemberCount() - 1);
            circleRepository.save(circle);
        }
        
        return Map.of(
                "isMember", false,
                "memberCount", circle.getMemberCount()
        );
    }
    
    /**
     * 检查是否是圈子成员
     */
    public boolean isMember(Integer circleId, Integer userId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return circleMemberRepository.existsByCircleAndUserAndIsActiveTrue(circle, user);
    }
    
    /**
     * 获取圈子成员列表
     */
    public Page<CircleMemberDto> getCircleMembers(Integer circleId, int page, int size) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return circleMemberRepository
                .findByCircleAndIsActiveTrueOrderByJoinedAtDesc(circle, pageable)
                .map(this::convertMemberToDto);
    }
    
    /**
     * 获取用户加入的圈子列表
     */
    public Page<CircleDto> getUserJoinedCircles(Integer userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return circleMemberRepository
                .findByUserAndIsActiveTrueOrderByJoinedAtDesc(user, pageable)
                .map(member -> convertToDto(member.getCircle()));
    }
    
    /**
     * 获取成员统计信息
     */
    public Map<String, Long> getMemberStats(Integer circleId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        long memberCount = circleMemberRepository.countByCircleAndIsActiveTrue(circle);
        
        return Map.of(
                "memberCount", memberCount,
                "postCount", (long) circle.getPostCount()
        );
    }
    
    private CircleDto convertToDto(Circle circle) {
        return CircleDto.builder()
                .id(circle.getCircleId())
                .name(circle.getName())
                .description(circle.getDescription())
                .avatarUrl(circle.getAvatarUrl())
                .coverImageUrl(circle.getCoverImageUrl())
                .memberCount(circle.getMemberCount())
                .postCount(circle.getPostCount())
                .category(circle.getCategory())
                .isOfficial(circle.getIsOfficial())
                .createdAt(circle.getCreatedAt())
                .build();
    }
    
    private CircleMemberDto convertMemberToDto(CircleMember member) {
        return CircleMemberDto.builder()
                .memberId(member.getMemberId())
                .userId(member.getUser().getUserId())
                .username(member.getUser().getUsername())
                .userAvatar(member.getUser().getAvatarUrl())
                .joinedAt(member.getJoinedAt())
                .postCount(member.getPostCount())
                .build();
    }
}
