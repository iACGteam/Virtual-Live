package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.CommunityPostDto;
import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 活动中心控制器
 */
@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ActivityController {
    
    private final ActivityService activityService;
    
    /**
     * 获取圈子活动动态
     */
    @GetMapping("/circle/{circleId}")
    public R<Map<String, Object>> getCircleActivity(
            @PathVariable Integer circleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> activity = activityService.getCircleActivity(circleId, page, size);
            return R.ok(activity);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的动态（已加入圈子的帖子）
     */
    @GetMapping("/user/{userId}/feed")
    public R<Page<CommunityPostDto>> getUserFeed(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<CommunityPostDto> feed = activityService.getUserFeed(userId, page, size);
            return R.ok(feed);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取热门动态
     */
    @GetMapping("/trending")
    public R<Map<String, Object>> getTrending() {
        try {
            Map<String, Object> trending = activityService.getTrending();
            return R.ok(trending);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
