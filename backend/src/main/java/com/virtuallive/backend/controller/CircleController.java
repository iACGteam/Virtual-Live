package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.CircleDto;
import com.virtuallive.backend.model.dto.CircleMemberDto;
import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.service.CircleService;
import com.virtuallive.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/circles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CircleController {
    
    private final CircleService circleService;
    private final PostService postService;
    
    @GetMapping
    public R<Page<CircleDto>> getCircles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "new") String sort) {
        try {
            Page<CircleDto> circles = circleService.getCircles(page, size, sort);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public R<Page<CircleDto>> searchCircles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CircleDto> circles = circleService.searchCircles(keyword, page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/category/{category}")
    public R<Page<CircleDto>> getCirclesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CircleDto> circles = circleService.getCirclesByCategory(category, page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/official")
    public R<Page<CircleDto>> getOfficialCircles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CircleDto> circles = circleService.getOfficialCircles(page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public R<CircleDto> getCircle(@PathVariable Integer id) {
        try {
            CircleDto circle = circleService.getCircleById(id);
            return R.ok(circle);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 加入圈子
     */
    @PostMapping("/{circleId}/join")
    public R<Map<String, Object>> joinCircle(
            @PathVariable Integer circleId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            Map<String, Object> result = circleService.joinCircle(circleId, userId);
            return R.ok("加入成功", result);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 退出圈子
     */
    @PostMapping("/{circleId}/leave")
    public R<Map<String, Object>> leaveCircle(
            @PathVariable Integer circleId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            Map<String, Object> result = circleService.leaveCircle(circleId, userId);
            return R.ok("退出成功", result);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否是圈子成员
     */
    @GetMapping("/{circleId}/check-member")
    public R<Boolean> checkMember(
            @PathVariable Integer circleId,
            @RequestParam Integer userId) {
        try {
            boolean isMember = circleService.isMember(circleId, userId);
            return R.ok(isMember);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取圈子成员列表
     */
    @GetMapping("/{circleId}/members")
    public R<Page<CircleMemberDto>> getCircleMembers(
            @PathVariable Integer circleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<CircleMemberDto> members = circleService.getCircleMembers(circleId, page, size);
            return R.ok(members);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户加入的圈子列表
     */
    @GetMapping("/user/{userId}/joined")
    public R<Page<CircleDto>> getUserJoinedCircles(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<CircleDto> circles = circleService.getUserJoinedCircles(userId, page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取圈子统计信息
     */
    @GetMapping("/{circleId}/stats")
    public R<Map<String, Long>> getCircleStats(@PathVariable Integer circleId) {
        try {
            Map<String, Long> stats = circleService.getMemberStats(circleId);
            return R.ok(stats);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取圈子内的帖子列表
     */
    @GetMapping("/{circleId}/posts")
    public R<Page<VideoDto>> getCirclePosts(
            @PathVariable Integer circleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "new") String sort) {
        try {
            Page<VideoDto> posts = postService.getCirclePosts(circleId, page, size, sort);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
