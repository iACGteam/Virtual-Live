package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.VideoDto;
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
    
    public Page<VideoDto> getViewHistory(Integer userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewedAt"));
        // Only show history from last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        Page<ViewHistory> historyPage = viewHistoryRepository.findByUserUserIdAndViewedAtAfter(userId, thirtyDaysAgo, pageRequest);
        
        return historyPage.map(history -> {
            VideoDto dto = videoService.convertToDto(history.getVideo());
            // We might want to add viewedAt to DTO if needed, but VideoDto doesn't have it.
            // For now, just return the video info.
            return dto;
        });
    }
    
    @Transactional
    public void addViewHistory(Integer userId, Integer videoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
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
