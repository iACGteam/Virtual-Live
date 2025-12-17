package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.AuthResponse;
import com.virtuallive.backend.model.dto.LoginRequest;
import com.virtuallive.backend.model.dto.RegisterRequest;
import com.virtuallive.backend.model.dto.UpdateProfileRequest;
import com.virtuallive.backend.model.dto.UserProfileDto;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.UserFollowsRepository;
import com.virtuallive.backend.repository.WalletRepository;
import com.virtuallive.backend.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户业务层（适配新DTO+JPA风格）
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final UserFollowsRepository userFollowsRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户注册（适配RegisterRequest DTO）
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. 校验用户名是否存在
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    throw new RuntimeException("用户名已存在");
                });

        // 2. 校验邮箱是否存在
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("邮箱已存在");
                });

        // 3. 构建用户实体
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .registrationDate(new Date())
                .userType(0) // 默认用户类型
                .avatarUrl(request.getAvatarUrl()) // 可选字段
                .introduction(request.getIntroduction()) // 可选字段
                .build();

        // 4. 保存用户
        User savedUser = userRepository.save(user);

        // 5. 初始化钱包（默认10000元）
        walletRepository.initWallet(savedUser.getUserId());

        // 6. 生成Token
        String token = jwtUtils.generateToken(savedUser.getUserId(), savedUser.getUsername());

        // 7. 构建响应DTO
        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .message("注册成功")
                .build();
    }

    /**
     * 用户登录（适配LoginRequest DTO）
     */
    public AuthResponse login(LoginRequest request) {
        // 1. 查询用户（用户名/邮箱）
        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 更新最后登录时间
        user.setLastLoginTime(new Date());
        userRepository.save(user);

        // 4. 生成Token
        String token = jwtUtils.generateToken(user.getUserId(), user.getUsername());

        // 5. 构建响应DTO
        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .message("登录成功")
                .build();
    }

    /**
     * 获取用户基础信息
     */
    public User getUserInfo(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 更新用户信息（适配UpdateProfileRequest DTO）
     */
    @Transactional
    public UserProfileDto updateProfile(Integer userId, UpdateProfileRequest request) {
        // 1. 查询用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 更新用户名（校验唯一性）
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            userRepository.findByUsername(request.getName())
                    .ifPresent(u -> {
                        if (!u.getUserId().equals(userId)) {
                            throw new RuntimeException("用户名已被使用");
                        }
                    });
            user.setUsername(request.getName());
        }

        // 3. 更新个性签名
        if (request.getSignature() != null && !request.getSignature().trim().isEmpty()) {
            user.setIntroduction(request.getSignature());
        }

        // 4. 更新密码（校验两次密码一致）
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("两次输入的密码不一致");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // 5. 更新头像
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // 6. 保存修改
        userRepository.save(user);

        // 7. 返回更新后的用户信息
        return getUserProfile(userId);
    }

    /**
     * 获取用户主页信息（适配UserProfileDto）
     */
    public UserProfileDto getUserProfile(Integer userId) {
        // 1. 查询用户基础信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 查询关注数/粉丝数
        long followingCount = userFollowsRepository.countFollowing(userId);
        long followerCount = userFollowsRepository.countFollowers(userId);

        // 3. 圈子数（暂未实现，返回0）
        long circlesCount = 0;

        // 4. 获赞数（暂未实现，返回0）
        long likesCount = 0;

        // 5. 构建Profile DTO
        return UserProfileDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .introduction(user.getIntroduction())
                .userType(String.valueOf(user.getUserType()))
                .followersCount(followerCount)
                .followingCount(followingCount)
                .circlesCount(circlesCount)
                .likesCount(likesCount)
                .build();
    }

    /**
     * 获取关注列表
     */
    public List<User> getFollowingList(Integer userId) {
        return userFollowsRepository.findFollowingUsers(userId);
    }

    /**
     * 获取粉丝列表
     */
    public List<User> getFollowerList(Integer userId) {
        return userFollowsRepository.findFollowerUsers(userId);
    }
}
