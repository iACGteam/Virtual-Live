package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoController {
    
    private final VideoService videoService;
    
    @GetMapping
    public R<Page<VideoDto>> getVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sort) {
        try {
            Page<VideoDto> videos = videoService.getVideos(page, size, sort);
            return R.ok(videos);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public R<VideoDto> getVideo(@PathVariable Long id) {
        try {
            VideoDto video = videoService.getVideoById(id);
            return R.ok(video);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/category/{category}")
    public R<List<VideoDto>> getVideosByCategory(@PathVariable String category) {
        try {
            List<VideoDto> videos = videoService.getVideosByCategory(category);
            return R.ok(videos);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
