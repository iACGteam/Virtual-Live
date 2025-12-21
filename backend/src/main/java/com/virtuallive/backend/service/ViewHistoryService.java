package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.dto.ViewHistoryDto;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.model.entity.ViewHistory;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.VideoRepository;
import com.virtuallive.backend.repository.ViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ViewHistoryService {
    
    private final ViewHistoryRepository viewHistoryRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final VideoService videoService; // To convert Video to VideoDto
    
    @Transactional(readOnly = true)
    public Page<ViewHistoryDto> getViewHistory(Integer userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewedAt"));
        // Only show history from last 3 days
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        
        Page<ViewHistory> historyPage = viewHistoryRepository.findPublicHistoryByUserAndDate(userId, threeDaysAgo, pageRequest);
        
        return historyPage.map(history -> {
            VideoDto videoDto = videoService.convertToDto(history.getVideo());
            return ViewHistoryDto.builder()
                    .video(videoDto)
                    .viewedAt(history.getViewedAt())
                    .build();
        });
    }
    
    @Transactional
    public void addViewHistory(Integer userId, Integer videoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        // Check if video is private
        if (video.getTags() != null && video.getTags().contains("__PRIVATE__")) {
            return; // Do not add private videos to history
        }
        
        // Check if recently viewed (e.g. today), if so update time?
        // For simplicity, just add new record.
        
        ViewHistory history = ViewHistory.builder()
                .user(user)
                .video(video)
                .build();
        
        viewHistoryRepository.save(history);
    }
    
    @Transactional
    public void clearHistory(Integer userId) {
        viewHistoryRepository.deleteByUserUserId(userId);
    }
}
