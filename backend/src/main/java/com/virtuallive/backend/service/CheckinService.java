package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.model.entity.DailyCheckin;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.CircleRepository;
import com.virtuallive.backend.repository.DailyCheckinRepository;
import com.virtuallive.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckinService {
    
    private final DailyCheckinRepository checkinRepository;
    private final UserRepository userRepository;
    private final CircleRepository circleRepository;
    
    /**
     * 签到
     */
    @Transactional
    public DailyCheckin checkin(Integer userId, Integer circleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        LocalDate today = LocalDate.now();
        
        // 检查今天是否已签到
        if (checkinRepository.existsByUserAndCircleAndCheckinDate(user, circle, today)) {
            throw new RuntimeException("今天已经签到过了");
        }
        
        // 获取最后一次签到记录
        Optional<DailyCheckin> lastCheckin = checkinRepository.findLastCheckinByUserAndCircle(user, circle);
        
        int continuousDays = 1;
        long totalDays = checkinRepository.countByUserAndCircle(user, circle) + 1;
        
        if (lastCheckin.isPresent()) {
            LocalDate lastCheckinDate = lastCheckin.get().getCheckinDate();
            long daysBetween = ChronoUnit.DAYS.between(lastCheckinDate, today);
            
            if (daysBetween == 1) {
                // 连续签到
                continuousDays = lastCheckin.get().getContinuousDays() + 1;
            } else if (daysBetween > 1) {
                // 中断了，重新开始
                continuousDays = 1;
            }
        }
        
        // 计算奖励积分（连续签到有额外奖励）
        int rewardPoints = calculateRewardPoints(continuousDays);
        
        // 创建签到记录
        DailyCheckin checkin = DailyCheckin.builder()
                .user(user)
                .circle(circle)
                .checkinDate(today)
                .continuousDays(continuousDays)
                .totalDays((int) totalDays)
                .rewardPoints(rewardPoints)
                .build();
        
        DailyCheckin saved = checkinRepository.save(checkin);
        log.info("用户 {} 在圈子 {} 签到成功，连续 {} 天", userId, circleId, continuousDays);
        
        return saved;
    }
    
    /**
     * 检查今天是否已签到
     */
    public boolean hasCheckedInToday(Integer userId, Integer circleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        return checkinRepository.existsByUserAndCircleAndCheckinDate(user, circle, LocalDate.now());
    }
    
    /**
     * 获取连续签到天数
     */
    public int getContinuousDays(Integer userId, Integer circleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        Optional<DailyCheckin> lastCheckin = checkinRepository.findLastCheckinByUserAndCircle(user, circle);
        
        if (lastCheckin.isEmpty()) {
            return 0;
        }
        
        LocalDate lastCheckinDate = lastCheckin.get().getCheckinDate();
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(lastCheckinDate, today);
        
        // 如果最后一次签到是今天或昨天，返回连续天数
        if (daysBetween <= 1) {
            return lastCheckin.get().getContinuousDays();
        }
        
        // 否则连续签到已中断
        return 0;
    }
    
    /**
     * 获取总签到天数
     */
    public long getTotalCheckinDays(Integer userId, Integer circleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        
        return checkinRepository.countByUserAndCircle(user, circle);
    }
    
    /**
     * 计算签到奖励积分
     */
    private int calculateRewardPoints(int continuousDays) {
        int basePoints = 10; // 基础积分
        int bonusPoints = 0;
        
        // 连续签到奖励
        if (continuousDays >= 7) {
            bonusPoints = 20;
        } else if (continuousDays >= 3) {
            bonusPoints = 10;
        }
        
        return basePoints + bonusPoints;
    }
}
