package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.CircleDto;
import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.service.CircleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/circles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CircleController {
    
    private final CircleService circleService;
    
    @GetMapping
    public R<Page<CircleDto>> getCircles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "new") String sort) {
        try {
            Page<CircleDto> circles = circleService.getCircles(page, size, sort);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public R<Page<CircleDto>> searchCircles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CircleDto> circles = circleService.searchCircles(keyword, page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/category/{category}")
    public R<Page<CircleDto>> getCirclesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CircleDto> circles = circleService.getCirclesByCategory(category, page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/official")
    public R<Page<CircleDto>> getOfficialCircles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CircleDto> circles = circleService.getOfficialCircles(page, size);
            return R.ok(circles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public R<CircleDto> getCircle(@PathVariable Integer id) {
        try {
            CircleDto circle = circleService.getCircleById(id);
            return R.ok(circle);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
