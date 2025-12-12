package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.UserFollow;
import com.virtuallive.backend.repository.UserFollowRepository;
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
public class FollowService {
    
    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    
    /**
     * 关注/取消关注
     */
    @Transactional
    public boolean toggleFollow(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("关注者不存在"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("被关注者不存在"));
        
        Optional<UserFollow> existingFollow = userFollowRepository.findByFollowerAndFollowing(follower, following);
        
        if (existingFollow.isPresent()) {
            // 已关注，取消关注
            userFollowRepository.delete(existingFollow.get());
            log.info("用户 {} 取消关注用户 {}", followerId, followingId);
            return false;
        } else {
            // 未关注，添加关注
            UserFollow follow = UserFollow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            userFollowRepository.save(follow);
            log.info("用户 {} 关注用户 {}", followerId, followingId);
            return true;
        }
    }
    
    /**
     * 检查是否已关注
     */
    public boolean isFollowing(Integer followerId, Integer followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return userFollowRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    /**
     * 获取粉丝列表
     */
    public Page<UserFollow> getFollowers(Integer userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return userFollowRepository.findByFollowingOrderByFollowDateDesc(user, pageable);
    }
    
    /**
     * 获取关注列表
     */
    public Page<UserFollow> getFollowing(Integer userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return userFollowRepository.findByFollowerOrderByFollowDateDesc(user, pageable);
    }
    
    /**
     * 获取粉丝数
     */
    public long getFollowerCount(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return userFollowRepository.countByFollowing(user);
    }
    
    /**
     * 获取关注数
     */
    public long getFollowingCount(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return userFollowRepository.countByFollower(user);
    }
}
