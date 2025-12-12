package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.entity.UserFollow;
import com.virtuallive.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 关注控制器
 */
@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FollowController {
    
    private final FollowService followService;
    
    /**
     * 关注/取消关注用户
     */
    @PostMapping("/{followingId}")
    public R<Map<String, Object>> toggleFollow(
            @PathVariable Integer followingId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer followerId = body.get("userId");
            boolean isFollowing = followService.toggleFollow(followerId, followingId);
            long followerCount = followService.getFollowerCount(followingId);
            
            return R.ok("操作成功", Map.of(
                    "isFollowing", isFollowing,
                    "followerCount", followerCount
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已关注
     */
    @GetMapping("/check")
    public R<Boolean> checkFollow(
            @RequestParam Integer followerId,
            @RequestParam Integer followingId) {
        try {
            boolean isFollowing = followService.isFollowing(followerId, followingId);
            return R.ok(isFollowing);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取粉丝列表
     */
    @GetMapping("/followers/{userId}")
    public R<Page<UserFollow>> getFollowers(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<UserFollow> followers = followService.getFollowers(userId, page, size);
            return R.ok(followers);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取关注列表
     */
    @GetMapping("/following/{userId}")
    public R<Page<UserFollow>> getFollowing(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<UserFollow> following = followService.getFollowing(userId, page, size);
            return R.ok(following);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取粉丝数和关注数
     */
    @GetMapping("/count/{userId}")
    public R<Map<String, Long>> getFollowCount(@PathVariable Integer userId) {
        try {
            long followerCount = followService.getFollowerCount(userId);
            long followingCount = followService.getFollowingCount(userId);
            
            return R.ok(Map.of(
                    "followerCount", followerCount,
                    "followingCount", followingCount
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
