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
@Transactional(readOnly = true)
public class CircleService {
    
    private final CircleRepository circleRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final UserRepository userRepository;
    
    public Page<CircleDto> getCircles(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Circle> circles;
        if ("hot".equals(sort) || "popular".equals(sort)) {
            circles = circleRepository.findPopularCircles(pageable);
        } else if ("random".equals(sort)) {
            circles = circleRepository.findRandomCircles(pageable);
        } else {
            circles = circleRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        }
        
        return circles.map(this::convertToDto);
    }

    @Transactional
    public CircleDto createCircle(com.virtuallive.backend.model.dto.CircleCreateDto createDto) {
        // Validate user
        if (!userRepository.existsById(createDto.getCreatorId())) {
            throw new RuntimeException("用户不存在");
        }

        Circle circle = Circle.builder()
                .name(createDto.getName())
                .description(createDto.getDescription())
                .coverImageUrl(createDto.getCoverImageUrl())
                .avatarUrl(userRepository.findById(createDto.getCreatorId()).map(User::getAvatarUrl).orElse(createDto.getAvatarUrl()))
                .category(createDto.getCategory())
                .creatorId(createDto.getCreatorId())
                .memberCount(0) // Creator is not counted as a fan
                .build();
        
        circle = circleRepository.save(circle);
        
        // Manually add creator as member without incrementing count
        User user = userRepository.findById(createDto.getCreatorId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
                
        CircleMember member = CircleMember.builder()
                .circle(circle)
                .user(user)
                .postCount(0)
                .isActive(true)
                .build();
        
        circleMemberRepository.save(member);
        
        return convertToDto(circle);
    }

    public Page<CircleDto> getMyCircles(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.findByCreatorIdAndIsActiveTrue(userId, pageable)
                .map(this::convertToDto);
    }

    @Transactional
    public void dissolveCircle(Integer circleId, Integer userId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        if (circle.getCreatorId() != null && !circle.getCreatorId().equals(userId)) {
            throw new RuntimeException("只有圈主可以解散圈子");
        }
        
        circle.setIsActive(false); // Soft delete
        circleRepository.save(circle);
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
        
        // 查找任何状态的成员记录
        Optional<CircleMember> existingMemberOpt = circleMemberRepository
                .findByCircleAndUser(circle, user);
        
        CircleMember member;
        if (existingMemberOpt.isPresent()) {
            member = existingMemberOpt.get();
            if (Boolean.TRUE.equals(member.getIsActive())) {
                throw new RuntimeException("已经是圈子成员");
            }
            // 重新激活
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
                .findByUserAndIsActiveTrueAndCircle_IsActiveTrueOrderByJoinedAtDesc(user, pageable)
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
        // 动态获取创建者的最新头像
        String creatorAvatar = circle.getAvatarUrl();
        if (circle.getCreatorId() != null) {
            creatorAvatar = userRepository.findById(circle.getCreatorId())
                    .map(User::getAvatarUrl)
                    .orElse(circle.getAvatarUrl());
        }

        return CircleDto.builder()
                .id(circle.getCircleId())
                .name(circle.getName())
                .description(circle.getDescription())
                .avatarUrl(creatorAvatar)
                .coverImageUrl(circle.getCoverImageUrl())
                .memberCount(circle.getMemberCount())
                .postCount(circle.getPostCount())
                .category(circle.getCategory())
                .isOfficial(circle.getIsOfficial())
                .creatorId(circle.getCreatorId())
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
