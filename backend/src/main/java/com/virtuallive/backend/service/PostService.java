package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.dto.VideoUploadDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 社区帖子服务
 * 处理帖子的CRUD、搜索、多媒体上传等业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoProcessingService videoProcessingService;
    
    private static final String UPLOAD_DIR = "uploads/";
    private static final String IMAGE_DIR = UPLOAD_DIR + "images/";
    private static final String VIDEO_DIR = UPLOAD_DIR + "videos/";
    
    /**
     * 获取帖子列表
     */
    public Page<VideoDto> getPosts(int page, int size, String sort) {
        Pageable pageable;
        
        switch (sort) {
            case "popular":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "views", "likes"));
                break;
            case "hot":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes", "commentsCount"));
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        Page<Video> videos = videoRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        return videos.map(this::convertToDto);
    }
    
    /**
     * 全局搜索帖子
     */
    public Page<VideoDto> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videos = videoRepository.searchByKeyword(keyword, pageable);
        return videos.map(this::convertToDto);
    }
    
    /**
     * 获取帖子详情
     */
    @Transactional
    public VideoDto getPostById(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        if (video.getIsDeleted()) {
            throw new RuntimeException("帖子已被删除");
        }
        
        // 增加浏览量
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);
        
        return convertToDto(video);
    }
    
    /**
     * 获取用户的帖子列表
     */
    public Page<VideoDto> getUserPosts(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videos = videoRepository.findByAuthor_UserIdAndIsDeletedFalse(userId, pageable);
        return videos.map(this::convertToDto);
    }
    
    /**
     * 获取圈子的帖子列表
     */
    public Page<VideoDto> getCirclePosts(Integer circleId, int page, int size, String sort) {
        Pageable pageable;
        
        switch (sort) {
            case "popular":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "views", "likes"));
                break;
            case "hot":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes", "commentsCount"));
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        Page<Video> videos = videoRepository.findByCircle_CircleIdAndIsDeletedFalse(circleId, pageable);
        return videos.map(this::convertToDto);
    }
    
    /**
     * 创建帖子
     */
    @Transactional
    public VideoDto createPost(VideoUploadDto uploadDto) {
        User author = userRepository.findById(uploadDto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Video.VideoBuilder videoBuilder = Video.builder()
                .author(author)
                .title(uploadDto.getTitle())
                .content(uploadDto.getContent())
                .category(uploadDto.getCategory())
                .tags(uploadDto.getTags())
                .coverImageUrl(uploadDto.getCoverImageUrl())
                .videoUrl(uploadDto.getVideoUrl());
        
        Video video = videoBuilder.build();
        
        // 如果有视频URL，处理视频元数据
        if (uploadDto.getVideoUrl() != null && !uploadDto.getVideoUrl().isEmpty()) {
            try {
                VideoProcessingService.VideoInfo videoInfo = 
                    videoProcessingService.processVideo(uploadDto.getVideoUrl());
                
                if (videoInfo.getDuration() != null) {
                    video.setDuration(videoInfo.getDuration());
                }
                
                if (uploadDto.getCoverImageUrl() == null && videoInfo.getThumbnailUrl() != null) {
                    video.setCoverImageUrl(videoInfo.getThumbnailUrl());
                }
            } catch (Exception e) {
                log.warn("处理视频元数据失败: {}", e.getMessage());
            }
        }
        
        video = videoRepository.save(video);
        log.info("创建帖子成功: id={}, title={}", video.getPostId(), video.getTitle());
        
        return convertToDto(video);
    }
    
    /**
     * 更新帖子
     */
    @Transactional
    public VideoDto updatePost(Integer id, VideoUploadDto uploadDto) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        if (video.getIsDeleted()) {
            throw new RuntimeException("帖子已被删除");
        }
        
        // 更新字段
        if (uploadDto.getTitle() != null) {
            video.setTitle(uploadDto.getTitle());
        }
        if (uploadDto.getContent() != null) {
            video.setContent(uploadDto.getContent());
        }
        if (uploadDto.getCategory() != null) {
            video.setCategory(uploadDto.getCategory());
        }
        if (uploadDto.getTags() != null) {
            video.setTags(uploadDto.getTags());
        }
        if (uploadDto.getCoverImageUrl() != null) {
            video.setCoverImageUrl(uploadDto.getCoverImageUrl());
        }
        if (uploadDto.getVideoUrl() != null) {
            video.setVideoUrl(uploadDto.getVideoUrl());
        }
        
        video = videoRepository.save(video);
        log.info("更新帖子成功: id={}", id);
        
        return convertToDto(video);
    }
    
    /**
     * 删除帖子（软删除）
     */
    @Transactional
    public void deletePost(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        video.setIsDeleted(true);
        videoRepository.save(video);
        log.info("删除帖子成功: id={}", id);
    }
    
    /**
     * 点赞帖子
     */
    @Transactional
    public void likePost(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        video.setLikes(video.getLikes() + 1);
        videoRepository.save(video);
    }
    
    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        validateImageFile(file);
        return saveFile(file, IMAGE_DIR);
    }
    
    /**
     * 上传视频
     */
    public String uploadVideo(MultipartFile file) {
        validateVideoFile(file);
        return saveFile(file, VIDEO_DIR);
    }
    
    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, String directory) {
        try {
            // 创建目录
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());
            
            // 返回访问URL
            String fileUrl = "/" + directory + filename;
            log.info("文件上传成功: {}", fileUrl);
            
            return fileUrl;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持图片格式");
        }
        
        // 限制文件大小为10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过10MB");
        }
    }
    
    /**
     * 验证视频文件
     */
    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("只支持视频格式");
        }
        
        // 限制文件大小为500MB
        if (file.getSize() > 500 * 1024 * 1024) {
            throw new RuntimeException("视频大小不能超过500MB");
        }
    }
    
    /**
     * 转换为DTO
     */
    private VideoDto convertToDto(Video video) {
        VideoDto.VideoDtoBuilder builder = VideoDto.builder()
                .id(video.getPostId())
                .title(video.getTitle())
                .content(video.getContent())
                .coverImageUrl(video.getCoverImageUrl())
                .videoUrl(video.getVideoUrl())
                .duration(video.getDuration())
                .category(video.getCategory())
                .tags(video.getTags())
                .likes(video.getLikes())
                .views(video.getViews())
                .commentsCount(video.getCommentsCount())
                .createdAt(video.getCreatedAt())
                .authorId(video.getAuthor().getUserId())
                .authorName(video.getAuthor().getUsername())
                .authorAvatar(video.getAuthor().getAvatarUrl());
        
        if (video.getCircle() != null) {
            builder.circleId(video.getCircle().getCircleId())
                   .circleName(video.getCircle().getName());
        }
        
        return builder.build();
    }
}
