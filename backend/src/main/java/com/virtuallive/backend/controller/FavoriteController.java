package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.entity.Favorite;
import com.virtuallive.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    /**
     * 收藏/取消收藏视频
     */
    @PostMapping("/video/{videoId}")
    public R<Map<String, Object>> toggleVideoFavorite(
            @PathVariable Integer videoId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            boolean isFavorited = favoriteService.toggleFavorite(userId, videoId, Favorite.ContentType.post);
            long favoriteCount = favoriteService.getFavoriteCount(videoId, Favorite.ContentType.post);
            
            return R.ok("操作成功", Map.of(
                    "isFavorited", isFavorited,
                    "favoriteCount", favoriteCount
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public R<Boolean> checkFavorite(
            @RequestParam Integer userId,
            @RequestParam Integer contentId,
            @RequestParam String contentType) {
        try {
            Favorite.ContentType type = Favorite.ContentType.valueOf(contentType);
            boolean isFavorited = favoriteService.isFavorited(userId, contentId, type);
            return R.ok(isFavorited);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取收藏数
     */
    @GetMapping("/count")
    public R<Long> getFavoriteCount(
            @RequestParam Integer contentId,
            @RequestParam String contentType) {
        try {
            Favorite.ContentType type = Favorite.ContentType.valueOf(contentType);
            long count = favoriteService.getFavoriteCount(contentId, type);
            return R.ok(count);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/user/{userId}")
    public R<Page<Favorite>> getUserFavorites(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "post") String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Favorite.ContentType type = Favorite.ContentType.valueOf(contentType);
            Page<Favorite> favorites = favoriteService.getUserFavorites(userId, type, page, size);
            return R.ok(favorites);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
