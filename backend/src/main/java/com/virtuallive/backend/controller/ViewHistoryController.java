package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ViewHistoryController {
    
    private final ViewHistoryService viewHistoryService;
    
    @GetMapping("/user/{userId}")
    public R<Page<VideoDto>> getViewHistory(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<VideoDto> history = viewHistoryService.getViewHistory(userId, page, size);
            return R.ok(history);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PostMapping("/user/{userId}/video/{videoId}")
    public R<Void> addViewHistory(
            @PathVariable Integer userId,
            @PathVariable Integer videoId) {
        try {
            viewHistoryService.addViewHistory(userId, videoId);
            return R.ok("记录成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/user/{userId}")
    public R<Void> clearHistory(@PathVariable Integer userId) {
        try {
            viewHistoryService.clearHistory(userId);
            return R.ok("清空成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
