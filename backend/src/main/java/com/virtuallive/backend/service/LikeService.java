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
    private final VideoService videoService;
    
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
            // Check if video is private before liking
            if (contentType == Like.ContentType.post) {
                Video video = videoRepository.findById(contentId)
                        .orElseThrow(() -> new RuntimeException("视频不存在"));
                if (video.getTags() != null && video.getTags().contains("__PRIVATE__")) {
                    throw new RuntimeException("私密作品不允许点赞");
                }
            }

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

    /**
     * 获取用户点赞的视频列表
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.virtuallive.backend.model.dto.VideoDto> getLikedVideos(Integer userId, int page, int size) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        // Use the new query method to filter out private videos
        org.springframework.data.domain.Page<com.virtuallive.backend.model.entity.Video> videos = likeRepository.findLikedVideos(userId, Like.ContentType.post, pageable);
        
        return videos.map(videoService::convertToDto);
    }
}
