package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.entity.DailyCheckin;
import com.virtuallive.backend.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 签到控制器
 */
@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckinController {
    
    private final CheckinService checkinService;
    
    /**
     * 签到
     */
    @PostMapping("/circle/{circleId}")
    public R<DailyCheckin> checkin(
            @PathVariable Integer circleId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            DailyCheckin checkin = checkinService.checkin(userId, circleId);
            return R.ok("签到成功", checkin);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 检查今天是否已签到
     */
    @GetMapping("/circle/{circleId}/check")
    public R<Boolean> checkToday(
            @PathVariable Integer circleId,
            @RequestParam Integer userId) {
        try {
            boolean hasCheckedIn = checkinService.hasCheckedInToday(userId, circleId);
            return R.ok(hasCheckedIn);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取签到信息
     */
    @GetMapping("/circle/{circleId}/info")
    public R<Map<String, Object>> getCheckinInfo(
            @PathVariable Integer circleId,
            @RequestParam Integer userId) {
        try {
            int continuousDays = checkinService.getContinuousDays(userId, circleId);
            long totalDays = checkinService.getTotalCheckinDays(userId, circleId);
            boolean hasCheckedInToday = checkinService.hasCheckedInToday(userId, circleId);
            
            return R.ok(Map.of(
                    "continuousDays", continuousDays,
                    "totalDays", totalDays,
                    "hasCheckedInToday", hasCheckedInToday
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
