package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.Like;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.repository.LikeRepository;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    
    /**
     * 点赞/取消点赞
     */
    @Transactional
    public boolean toggleLike(Integer userId, Integer contentId, Like.ContentType contentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Optional<Like> existingLike = likeRepository.findByUserAndContentTypeAndContentId(
                user, contentType, contentId);
        
        if (existingLike.isPresent()) {
            // 已点赞，取消点赞
            likeRepository.delete(existingLike.get());
            
            // 更新内容的点赞数
            if (contentType == Like.ContentType.post) {
                updateVideoLikeCount(contentId, -1);
            }
            
            log.info("用户 {} 取消点赞 {} {}", userId, contentType, contentId);
            return false;
        } else {
            // 未点赞，添加点赞
            Like like = Like.builder()
                    .user(user)
                    .contentType(contentType)
                    .contentId(contentId)
                    .build();
            likeRepository.save(like);
            
            // 更新内容的点赞数
            if (contentType == Like.ContentType.post) {
                updateVideoLikeCount(contentId, 1);
            }
            
            log.info("用户 {} 点赞 {} {}", userId, contentType, contentId);
            return true;
        }
    }
    
    /**
     * 检查是否已点赞
     */
    public boolean isLiked(Integer userId, Integer contentId, Like.ContentType contentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return likeRepository.existsByUserAndContentTypeAndContentId(user, contentType, contentId);
    }
    
    /**
     * 获取点赞数
     */
    public long getLikeCount(Integer contentId, Like.ContentType contentType) {
        return likeRepository.countByContentTypeAndContentId(contentType, contentId);
    }
    
    /**
     * 更新视频点赞数
     */
    private void updateVideoLikeCount(Integer videoId, int delta) {
        videoRepository.findById(videoId).ifPresent(video -> {
            video.setLikes(Math.max(0, video.getLikes() + delta));
            videoRepository.save(video);
        });
    }
}
