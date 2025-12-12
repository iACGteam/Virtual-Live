package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {
    
    private final SearchService searchService;
    
    /**
     * 全局搜索
     */
    @GetMapping
    public R<Map<String, Object>> globalSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> result = searchService.globalSearch(keyword, page, size);
            return R.ok(result);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
