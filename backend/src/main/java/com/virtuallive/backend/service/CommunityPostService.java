package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.CommunityPostCreateDto;
import com.virtuallive.backend.model.dto.CommunityPostDto;
import com.virtuallive.backend.model.dto.CommunityPostUpdateDto;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社区帖子服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityPostService {
    
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoProcessingService videoProcessingService;
    
    /**
     * 获取社区帖子列表
     */
    public Page<CommunityPostDto> getPosts(int page, int size, String sort, Integer circleId) {
        Pageable pageable;
        
        if ("hot".equals(sort) || "popular".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "views", "likes"));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        Page<Video> posts;
        if (circleId != null) {
            // TODO: 实现圈子过滤（需要在Video实体中添加circleId字段）
            posts = videoRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        } else {
            posts = videoRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        
        return posts.map(this::convertToDto);
    }
    
    /**
     * 全局搜索帖子
     */
    public Page<CommunityPostDto> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // TODO: 实现全文搜索（标题、内容、标签）
        Page<Video> posts = videoRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        return posts.map(this::convertToDto);
    }
    
    /**
     * 获取帖子详情
     */
    public CommunityPostDto getPostById(Integer id) {
        Video post = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        if (post.getIsDeleted()) {
            throw new RuntimeException("帖子已被删除");
        }
        
        // 增加浏览量
        post.setViews(post.getViews() + 1);
        videoRepository.save(post);
        
        return convertToDto(post);
    }
    
    /**
     * 创建帖子
     */
    @Transactional
    public CommunityPostDto createPost(CommunityPostCreateDto createDto) {
        User author = userRepository.findById(createDto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Video post = Video.builder()
                .author(author)
                .title(createDto.getTitle())
                .content(createDto.getContent())
                .category(createDto.getCategory())
                .tags(createDto.getTags())
                .build();
        
        // 处理封面
        if (createDto.getCoverImageUrl() != null && !createDto.getCoverImageUrl().isEmpty()) {
            post.setCoverImageUrl(createDto.getCoverImageUrl());
        }
        
        // 处理视频
        if (createDto.getVideoUrl() != null && !createDto.getVideoUrl().isEmpty()) {
            post.setVideoUrl(createDto.getVideoUrl());
            
            // 自动提取视频时长和封面
            try {
                VideoProcessingService.VideoInfo videoInfo = videoProcessingService.processVideo(createDto.getVideoUrl());
                if (videoInfo.getDuration() != null) {
                    post.setDuration(videoInfo.getDuration());
                }
                if (videoInfo.getThumbnailUrl() != null && post.getCoverImageUrl() == null) {
                    post.setCoverImageUrl(videoInfo.getThumbnailUrl());
                }
            } catch (Exception e) {
                log.warn("视频处理失败: {}", e.getMessage());
            }
        }
        
        post = videoRepository.save(post);
        log.info("创建社区帖子成功: id={}, title={}, author={}", post.getPostId(), post.getTitle(), author.getUsername());
        
        return convertToDto(post);
    }
    
    /**
     * 更新帖子
     */
    @Transactional
    public CommunityPostDto updatePost(Integer id, CommunityPostUpdateDto updateDto) {
        Video post = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        if (post.getIsDeleted()) {
            throw new RuntimeException("帖子已被删除");
        }
        
        // 更新字段
        if (updateDto.getTitle() != null) {
            post.setTitle(updateDto.getTitle());
        }
        if (updateDto.getContent() != null) {
            post.setContent(updateDto.getContent());
        }
        if (updateDto.getCoverImageUrl() != null) {
            post.setCoverImageUrl(updateDto.getCoverImageUrl());
        }
        if (updateDto.getVideoUrl() != null) {
            post.setVideoUrl(updateDto.getVideoUrl());
        }
        if (updateDto.getCategory() != null) {
            post.setCategory(updateDto.getCategory());
        }
        if (updateDto.getTags() != null) {
            post.setTags(updateDto.getTags());
        }
        
        post = videoRepository.save(post);
        log.info("更新社区帖子成功: id={}, title={}", post.getPostId(), post.getTitle());
        
        return convertToDto(post);
    }
    
    /**
     * 删除帖子（软删除）
     */
    @Transactional
    public void deletePost(Integer id, Integer userId) {
        Video post = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        // 验证权限（只有作者可以删除）
        if (!post.getAuthor().getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此帖子");
        }
        
        post.setIsDeleted(true);
        videoRepository.save(post);
        log.info("删除社区帖子成功: id={}, title={}", post.getPostId(), post.getTitle());
    }
    
    /**
     * 点赞帖子
     */
    @Transactional
    public void likePost(Integer id) {
        Video post = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        post.setLikes(post.getLikes() + 1);
        videoRepository.save(post);
    }
    
    /**
     * 获取用户的帖子列表
     */
    public Page<CommunityPostDto> getUserPosts(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Video> posts = videoRepository.findByAuthor_UserIdAndIsDeletedFalse(userId);
        
        // 手动转换为Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), posts.size());
        List<CommunityPostDto> dtos = posts.subList(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, posts.size());
    }
    
    /**
     * 获取热门帖子
     */
    public List<CommunityPostDto> getTrendingPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "views", "likes"));
        Page<Video> posts = videoRepository.findPopularVideos(pageable);
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为DTO
     */
    private CommunityPostDto convertToDto(Video post) {
        CommunityPostDto dto = CommunityPostDto.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .coverImageUrl(post.getCoverImageUrl())
                .videoUrl(post.getVideoUrl())
                .duration(post.getDuration())
                .category(post.getCategory())
                .tags(post.getTags())
                .likes(post.getLikes())
                .views(post.getViews())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorId(post.getAuthor().getUserId())
                .authorName(post.getAuthor().getUsername())
                .authorAvatar(post.getAuthor().getAvatarUrl())
                .build();
        
        // 处理多图片（如果存储在coverImageUrl中，用分隔符分隔）
        if (post.getCoverImageUrl() != null && post.getCoverImageUrl().contains(",")) {
            dto.setImageUrls(Arrays.asList(post.getCoverImageUrl().split(",")));
        }
        
        return dto;
    }
}
