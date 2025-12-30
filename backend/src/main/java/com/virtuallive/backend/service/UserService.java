package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.UpdateProfileRequest;
import com.virtuallive.backend.model.dto.UserProfileDto;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.LikeRepository;
import com.virtuallive.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final FollowService followService;
    private final CircleService circleService;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserProfileDto getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        long followersCount = followService.getFollowerCount(userId);
        long followingCount = followService.getFollowingCount(userId);
        long circlesCount = circleService.getUserJoinedCircles(userId, 0, 1).getTotalElements();
        // 修改为统计用户点赞的数量（我喜欢的），而不是用户收到的点赞数
        // 统计用户发布的视频被点赞的总数（用户收到的获赞），而不是用户自己点赞的数量
        long likesCount = likeRepository.countVideoLikesByAuthorId(userId);
        
        return UserProfileDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .introduction(user.getIntroduction())
                .userType(user.getUserType().name())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .circlesCount(circlesCount)
                .likesCount(likesCount)
                .build();
    }
    
    @Transactional
    public UserProfileDto updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            // Check if username is taken by another user
            userRepository.findByUsername(request.getName())
                    .ifPresent(u -> {
                        if (!u.getUserId().equals(userId)) {
                            throw new RuntimeException("用户名已被使用");
                        }
                    });
            user.setUsername(request.getName());
        }
        
        if (request.getSignature() != null) {
            user.setIntroduction(request.getSignature());
        }
        
        if (request.getAvatar() != null && !request.getAvatar().trim().isEmpty()) {
            user.setAvatarUrl(request.getAvatar());
        }
        
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("两次输入的密码不一致");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        user = userRepository.save(user);
        
        return getUserProfile(userId);
    }
}
