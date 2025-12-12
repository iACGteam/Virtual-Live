package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.CircleDto;
import com.virtuallive.backend.model.dto.CommunityPostDto;
import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.repository.CircleRepository;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    
    private final VideoRepository videoRepository;
    private final CircleRepository circleRepository;
    private final UserRepository userRepository;
    private final VideoService videoService;
    private final CircleService circleService;
    private final CommunityPostService communityPostService;
    
    /**
     * 全局搜索
     */
    public Map<String, Object> globalSearch(String keyword, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        // 搜索视频
        Page<CommunityPostDto> posts = communityPostService.searchPosts(keyword, page, size);
        result.put("posts", posts);
        
        // 搜索圈子
        Page<CircleDto> circles = circleService.searchCircles(keyword, page, size);
        result.put("circles", circles);
        
        // 搜索用户
        Page<User> users = searchUsers(keyword, page, size);
        result.put("users", users);
        
        return result;
    }
    
    /**
     * 搜索用户
     */
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 这里需要在UserRepository中添加搜索方法
        // 暂时返回空结果
        return Page.empty(pageable);
    }
}
