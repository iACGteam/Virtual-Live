package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.entity.Like;
import com.virtuallive.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LikeController {
    
    private final LikeService likeService;
    
    /**
     * 点赞/取消点赞视频
     */
    @PostMapping("/video/{videoId}")
    public R<Map<String, Object>> toggleVideoLike(
            @PathVariable Integer videoId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            boolean isLiked = likeService.toggleLike(userId, videoId, Like.ContentType.post);
            long likeCount = likeService.getLikeCount(videoId, Like.ContentType.post);
            
            return R.ok("操作成功", Map.of(
                    "isLiked", isLiked,
                    "likeCount", likeCount
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/comment/{commentId}")
    public R<Map<String, Object>> toggleCommentLike(
            @PathVariable Integer commentId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            boolean isLiked = likeService.toggleLike(userId, commentId, Like.ContentType.comment);
            long likeCount = likeService.getLikeCount(commentId, Like.ContentType.comment);
            
            return R.ok("操作成功", Map.of(
                    "isLiked", isLiked,
                    "likeCount", likeCount
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已点赞
     */
    @GetMapping("/check")
    public R<Boolean> checkLike(
            @RequestParam Integer userId,
            @RequestParam Integer contentId,
            @RequestParam String contentType) {
        try {
            Like.ContentType type = Like.ContentType.valueOf(contentType);
            boolean isLiked = likeService.isLiked(userId, contentId, type);
            return R.ok(isLiked);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取点赞数
     */
    @GetMapping("/count")
    public R<Long> getLikeCount(
            @RequestParam Integer contentId,
            @RequestParam String contentType) {
        try {
            Like.ContentType type = Like.ContentType.valueOf(contentType);
            long count = likeService.getLikeCount(contentId, type);
            return R.ok(count);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
