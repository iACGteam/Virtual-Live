package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {
    
    private final VideoRepository videoRepository;
    
    public Page<VideoDto> getVideos(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Video> videos;
        if ("popular".equals(sort)) {
            videos = videoRepository.findPopularVideos(pageable);
        } else {
            videos = videoRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        
        return videos.map(this::convertToDto);
    }
    
    public VideoDto getVideoById(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        // 增加浏览量
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);
        
        return convertToDto(video);
    }
    
    public List<VideoDto> getVideosByCategory(String category) {
        Pageable pageable = PageRequest.of(0, 20);
        return videoRepository.findByCategoryAndIsDeletedFalse(category, pageable)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private VideoDto convertToDto(Video video) {
        return VideoDto.builder()
                .id(video.getPostId())
                .title(video.getTitle())
                .content(video.getContent())
                .coverImageUrl(video.getCoverImageUrl())
                .category(video.getCategory())
                .tags(video.getTags())
                .likes(video.getLikes())
                .views(video.getViews())
                .commentsCount(video.getCommentsCount())
                .createdAt(video.getCreatedAt())
                .authorId(video.getAuthor().getUserId())
                .authorName(video.getAuthor().getUsername())
                .authorAvatar(video.getAuthor().getAvatarUrl())
                .build();
    }
}
