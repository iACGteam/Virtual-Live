package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.dto.VideoUploadDto;
import com.virtuallive.backend.service.VideoService;
import jakarta.validation.Valid;
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
    public R<VideoDto> getVideo(@PathVariable Integer id) {
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

    /**
     * 创建新视频
     * 会自动提取视频封面和时长
     */
    @PostMapping
    public R<VideoDto> createVideo(@Valid @RequestBody VideoUploadDto uploadDto) {
        try {
            VideoDto video = videoService.createVideo(uploadDto);
            return R.ok(video);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新指定视频的元数据（封面和时长）
     */
    @PutMapping("/{id}/metadata")
    public R<VideoDto> updateVideoMetadata(@PathVariable Integer id) {
        try {
            VideoDto video = videoService.updateVideoMetadata(id);
            return R.ok(video);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 批量更新所有视频的元数据
     * 用于数据迁移或修复
     */
    @PostMapping("/batch-update-metadata")
    public R<String> batchUpdateMetadata() {
        try {
            videoService.updateAllVideosMetadata();
            return R.ok("批量更新已完成");
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
