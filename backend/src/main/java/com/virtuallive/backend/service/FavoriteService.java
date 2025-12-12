package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.Favorite;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.FavoriteRepository;
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
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    
    /**
     * 收藏/取消收藏
     */
    @Transactional
    public boolean toggleFavorite(Integer userId, Integer contentId, Favorite.ContentType contentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndContentTypeAndContentId(
                user, contentType, contentId);
        
        if (existingFavorite.isPresent()) {
            // 已收藏，取消收藏
            favoriteRepository.delete(existingFavorite.get());
            log.info("用户 {} 取消收藏 {} {}", userId, contentType, contentId);
            return false;
        } else {
            // 未收藏，添加收藏
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .contentType(contentType)
                    .contentId(contentId)
                    .build();
            favoriteRepository.save(favorite);
            log.info("用户 {} 收藏 {} {}", userId, contentType, contentId);
            return true;
        }
    }
    
    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Integer userId, Integer contentId, Favorite.ContentType contentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return favoriteRepository.existsByUserAndContentTypeAndContentId(user, contentType, contentId);
    }
    
    /**
     * 获取收藏数
     */
    public long getFavoriteCount(Integer contentId, Favorite.ContentType contentType) {
        return favoriteRepository.countByContentTypeAndContentId(contentType, contentId);
    }
    
    /**
     * 获取用户的收藏列表
     */
    public Page<Favorite> getUserFavorites(Integer userId, Favorite.ContentType contentType, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        return favoriteRepository.findByUserAndContentTypeOrderByCreatedAtDesc(user, contentType, pageable);
    }
}
