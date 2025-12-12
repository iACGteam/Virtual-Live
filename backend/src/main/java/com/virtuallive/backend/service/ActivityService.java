package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.CircleDto;
import com.virtuallive.backend.model.dto.CommunityPostDto;
import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.CircleMember;
import com.virtuallive.backend.repository.CircleMemberRepository;
import com.virtuallive.backend.repository.CircleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    
    private final CommunityPostService communityPostService;
    private final CircleRepository circleRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final CircleService circleService;
    
    /**
     * 获取圈子活动动态
     */
    public Map<String, Object> getCircleActivity(Integer circleId, int page, int size) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        Map<String, Object> activity = new HashMap<>();
        
        // 获取圈子信息
        CircleDto circleDto = circleService.getCircleById(circleId);
        activity.put("circle", circleDto);
        
        // 获取最新帖子
        Page<CommunityPostDto> latestPosts = communityPostService.getPosts(page, size, "time", circleId);
        activity.put("latestPosts", latestPosts);
        
        // 获取热门帖子
        Page<CommunityPostDto> hotPosts = communityPostService.getPosts(0, 5, "hot", circleId);
        activity.put("hotPosts", hotPosts);
        
        // 获取成员数
        long memberCount = circleMemberRepository.countByCircleAndIsActiveTrue(circle);
        activity.put("memberCount", memberCount);
        
        // 获取活跃成员（最近加入的）
        Pageable memberPageable = PageRequest.of(0, 10);
        Page<CircleMember> activeMembers = circleMemberRepository
                .findByCircleAndIsActiveTrueOrderByJoinedAtDesc(circle, memberPageable);
        activity.put("activeMembers", activeMembers);
        
        return activity;
    }
    
    /**
     * 获取用户的动态（已加入圈子的帖子）
     */
    public Page<CommunityPostDto> getUserFeed(Integer userId, int page, int size) {
        // 获取用户加入的圈子
        Pageable pageable = PageRequest.of(page, size);
        // TODO: 实现获取用户加入的圈子的帖子
        // 目前返回所有帖子
        return communityPostService.getPosts(page, size, "time", null);
    }
    
    /**
     * 获取热门动态
     */
    public Map<String, Object> getTrending() {
        Map<String, Object> trending = new HashMap<>();
        
        // 热门帖子
        Page<CommunityPostDto> hotPosts = communityPostService.getPosts(0, 10, "hot", null);
        trending.put("hotPosts", hotPosts);
        
        // 热门圈子
        Page<CircleDto> hotCircles = circleService.getCircles(0, 10, "hot");
        trending.put("hotCircles", hotCircles);
        
        return trending;
    }
}
