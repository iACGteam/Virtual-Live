package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.dto.VideoUploadDto;
import com.virtuallive.backend.model.dto.DanmakuDto;
import com.virtuallive.backend.live.entity.Danmaku;
import com.virtuallive.backend.live.repository.DanmakuRepository;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {
    
    private final VideoRepository videoRepository;
    private final VideoProcessingService videoProcessingService;
    private final UserRepository userRepository;
    private final DanmakuRepository danmakuRepository;

    @Transactional(readOnly = true)
    public Page<VideoDto> getVideos(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Video> videos;
        if ("popular".equals(sort)) {
            videos = videoRepository.findPopularVideos(pageable);
        } else {
            videos = videoRepository.findByCircleIsNullAndIsDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        
        return videos.map(this::convertToDto);
    }
    
    @Transactional
    public VideoDto getVideoById(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        // 移除在此处增加浏览量的逻辑，改为单独的接口调用
        // video.setViews(video.getViews() + 1);
        // videoRepository.save(video);
        
        return convertToDto(video);
    }

    @Transactional
    public void increaseViewCount(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);
    }
    
    @Transactional(readOnly = true)
    public List<VideoDto> getVideosByCategory(String category) {
        Pageable pageable = PageRequest.of(0, 20);
        return videoRepository.findByCategoryAndIsDeletedFalse(category, pageable)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public VideoDto convertToDto(Video video) {
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

    /**
     * 创建新视频
     * 自动提取封面和视频时长
     */
    @Transactional
    public VideoDto createVideo(VideoUploadDto uploadDto) {
        User author = userRepository.findById(uploadDto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 处理视频：提取封面和获取时长
        VideoProcessingService.VideoInfo videoInfo = videoProcessingService.processVideo(uploadDto.getVideoUrl());

        // 构建视频实体
        Video video = Video.builder()
                .author(author)
                .title(uploadDto.getTitle())
                .content(uploadDto.getContent())
                .videoUrl(uploadDto.getVideoUrl())
                .category(uploadDto.getCategory())
                .tags(uploadDto.getTags())
                .build();

        // 设置封面：优先使用用户提供的，否则使用自动提取的
        if (uploadDto.getCoverImageUrl() != null && !uploadDto.getCoverImageUrl().isEmpty()) {
            video.setCoverImageUrl(uploadDto.getCoverImageUrl());
        } else if (videoInfo.getThumbnailUrl() != null) {
            video.setCoverImageUrl(videoInfo.getThumbnailUrl());
        }

        // 设置视频时长
        if (videoInfo.getDuration() != null) {
            video.setDuration(videoInfo.getDuration());
        }

        video = videoRepository.save(video);
        log.info("创建视频成功: id={}, title={}, duration={}s", video.getPostId(), video.getTitle(), video.getDuration());

        return convertToDto(video);
    }

    /**
     * 更新现有视频的元数据（封面和时长）
     * 用于修复已有数据或更新视频信息
     */
    @Transactional
    public VideoDto updateVideoMetadata(Integer videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));

        String videoUrl = video.getVideoUrl();
        if (videoUrl == null || videoUrl.isEmpty()) {
            throw new RuntimeException("视频URL为空，无法处理");
        }

        // 重新处理视频
        VideoProcessingService.VideoInfo videoInfo = videoProcessingService.processVideo(videoUrl);

        // 更新封面（如果没有封面或需要重新生成）
        if (videoInfo.getThumbnailUrl() != null) {
            video.setCoverImageUrl(videoInfo.getThumbnailUrl());
        }

        // 更新时长
        if (videoInfo.getDuration() != null) {
            video.setDuration(videoInfo.getDuration());
        }

        video = videoRepository.save(video);
        log.info("更新视频元数据成功: id={}, duration={}s", video.getPostId(), video.getDuration());

        return convertToDto(video);
    }

    /**
     * 批量更新所有视频的元数据
     * 用于数据迁移或修复
     */
    @Transactional
    public void updateAllVideosMetadata() {
        List<Video> videos = videoRepository.findAll();
        log.info("开始批量更新 {} 个视频的元数据", videos.size());

        int successCount = 0;
        int failCount = 0;

        for (Video video : videos) {
            try {
                if (video.getVideoUrl() != null && !video.getVideoUrl().isEmpty()) {
                    VideoProcessingService.VideoInfo videoInfo = videoProcessingService.processVideo(video.getVideoUrl());

                    boolean updated = false;
                    if (videoInfo.getThumbnailUrl() != null && video.getCoverImageUrl() == null) {
                        video.setCoverImageUrl(videoInfo.getThumbnailUrl());
                        updated = true;
                    }

                    if (videoInfo.getDuration() != null) {
                        video.setDuration(videoInfo.getDuration());
                        updated = true;
                    }

                    if (updated) {
                        videoRepository.save(video);
                        successCount++;
                        log.info("更新视频 #{}: {}", video.getPostId(), video.getTitle());
                    }
                }
            } catch (Exception e) {
                failCount++;
                log.error("更新视频 #{} 失败", video.getPostId(), e);
            }
        }

        log.info("批量更新完成: 成功={}, 失败={}", successCount, failCount);
    }

    @Transactional
    public void deleteVideo(Integer id, Integer userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        if (!video.getAuthor().getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此视频");
        }
        
        video.setIsDeleted(true);
        videoRepository.save(video);
    }

    @Transactional
    public VideoDto updateVideo(Integer id, Integer userId, VideoUploadDto updateDto) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        if (!video.getAuthor().getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此视频");
        }
        
        if (updateDto.getTitle() != null) video.setTitle(updateDto.getTitle());
        if (updateDto.getContent() != null) video.setContent(updateDto.getContent());
        if (updateDto.getCategory() != null) video.setCategory(updateDto.getCategory());
        if (updateDto.getTags() != null) video.setTags(updateDto.getTags());
        if (updateDto.getCoverImageUrl() != null) video.setCoverImageUrl(updateDto.getCoverImageUrl());
        
        return convertToDto(videoRepository.save(video));
    }

    public List<DanmakuDto> getDanmaku(Integer videoId) {
        return danmakuRepository.findByVideoIdOrderByCreatedAtAsc(videoId).stream()
                .map(this::convertToDanmakuDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DanmakuDto sendDanmaku(Integer videoId, DanmakuDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Danmaku danmaku = new Danmaku();
        danmaku.setVideoId(videoId);
        danmaku.setUserId(user.getUserId());
        danmaku.setContent(dto.getText());
        danmaku.setColor(dto.getColor());
        danmaku.setVideoTime(dto.getTime());
        
        danmaku = danmakuRepository.save(danmaku);
        
        return convertToDanmakuDto(danmaku);
    }

    private DanmakuDto convertToDanmakuDto(Danmaku danmaku) {
        DanmakuDto dto = new DanmakuDto();
        dto.setId(danmaku.getDanmakuId());
        dto.setText(danmaku.getContent());
        dto.setColor(danmaku.getColor());
        dto.setTime(danmaku.getVideoTime());
        dto.setUserId(danmaku.getUserId());
        
        userRepository.findById(danmaku.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
            dto.setAvatarUrl(user.getAvatarUrl());
        });
        
        return dto;
    }
}
