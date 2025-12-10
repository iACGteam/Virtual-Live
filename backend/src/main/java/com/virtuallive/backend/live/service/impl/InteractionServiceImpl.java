package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.LeaderboardItemDTO;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.entity.*;
import com.virtuallive.backend.live.repository.*;
import com.virtuallive.backend.live.service.InteractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired private DanmakuRepository danmakuRepository;
    @Autowired private GiftDonationRepository giftDonationRepository;
    @Autowired private UserWalletRepository userWalletRepository;
    @Autowired private LiveSessionRepository liveSessionRepository;
    @Autowired private RoomBanRepository roomBanRepository;

    private Integer getCurrentSessionId(Integer roomId) {
        return liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(roomId)
                .map(LiveSession::getSessionId)
                .orElse(null);
    }

    // 检查是否被禁言
    public boolean isUserMuted(Integer roomId, Integer userId) {
        return roomBanRepository.findFirstByRoomIdAndUserIdAndEndTimeAfter(roomId, userId, LocalDateTime.now()).isPresent();
    }

    // 禁言用户
    public void muteUser(Integer roomId, Integer userId, int durationSeconds) {
        RoomBan ban = new RoomBan();
        ban.setRoomId(roomId);
        ban.setUserId(userId);
        ban.setEndTime(LocalDateTime.now().plusSeconds(durationSeconds));
        ban.setReason("主播/房管禁言");
        roomBanRepository.save(ban);
    }

    // 新增：解除禁言
    @Override
    public void unmuteUser(Integer roomId, Integer userId) {
        // 查找当前所有有效的禁言记录并使其过期
        Optional<RoomBan> banOpt = roomBanRepository.findFirstByRoomIdAndUserIdAndEndTimeAfter(roomId, userId, LocalDateTime.now());
        if (banOpt.isPresent()) {
            RoomBan ban = banOpt.get();
            // 修正：设置为过去的时间 (当前时间减1秒)，确保立即失效
            ban.setEndTime(LocalDateTime.now().minusSeconds(1));
            roomBanRepository.save(ban);
            log.info("用户[{}] 在房间[{}] 的禁言已解除", userId, roomId);
        } else {
            log.warn("未找到用户[{}] 在房间[{}] 的有效禁言记录", userId, roomId);
        }
    }

    // 删除弹幕
    public void deleteDanmaku(Integer danmakuId) {
        danmakuRepository.findById(danmakuId).ifPresent(d -> {
            d.setIsDeleted(true);
            danmakuRepository.save(d);
        });
    }

    @Override
    @Transactional
    public Integer saveDanmaku(DanmakuMessage message, UserInfoDTO user) {
        Integer sessionId = getCurrentSessionId(message.getRoomId());
        if (sessionId == null) sessionId = 0;

        Danmaku danmaku = new Danmaku();
        danmaku.setSessionId(sessionId);
        danmaku.setUserId(user.getUserId().intValue());
        danmaku.setContent(message.getContent());
        danmaku.setColor(message.getColor());
        danmaku = danmakuRepository.save(danmaku);

        return danmaku.getDanmakuId();
    }

    @Override
    @Transactional
    public void processGift(DanmakuMessage message, UserInfoDTO user) {
        BigDecimal price = BigDecimal.valueOf(message.getGiftPrice());
        // 如果是 SC，数量默认为 1
        int count = "SC".equalsIgnoreCase(message.getType()) ? 1 : message.getGiftCount();
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(count));
        Integer userId = user.getUserId().intValue();

        // SC 校验
        if ("SC".equalsIgnoreCase(message.getType())) {
            if (totalCost.compareTo(BigDecimal.valueOf(30)) < 0) {
                throw new RuntimeException("SC 最低由 30元 起送");
            }
            // 计算时长：1元 = 4秒
            int duration = totalCost.intValue() * 4;
            message.setScDuration(duration);
            // 可以在弹幕内容前加标识
            message.setContent("[SC " + duration + "s] " + message.getContent());
        }

        // 扣费逻辑
        UserWallet wallet = userWalletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("用户钱包不存在"));

        if (wallet.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("余额不足");
        }

        wallet.setBalance(wallet.getBalance().subtract(totalCost));
        wallet.setTotalSpent(wallet.getTotalSpent().add(totalCost));
        userWalletRepository.save(wallet);

        // 记录流水
        Integer sessionId = getCurrentSessionId(message.getRoomId());
        GiftDonation donation = new GiftDonation();
        donation.setSessionId(sessionId != null ? sessionId : 0);
        donation.setSenderId(userId);
        donation.setGiftType(message.getType()); // GIFT 或 SC
        donation.setGiftName(message.getGiftName());
        donation.setGiftValue(price);
        donation.setQuantity(count);
        donation.setTotalValue(totalCost);
        giftDonationRepository.save(donation);

        // 更新场次收益
        if (sessionId != null && sessionId != 0) {
            liveSessionRepository.findById(sessionId).ifPresent(session -> {
                BigDecimal currentRevenue = session.getTotalGiftsRevenue() == null ? BigDecimal.ZERO : session.getTotalGiftsRevenue();
                session.setTotalGiftsRevenue(currentRevenue.add(totalCost));
                liveSessionRepository.save(session);
            });
        }

        // 如果是 SC，同时也保存一份到弹幕表，方便历史记录回看
        if ("SC".equalsIgnoreCase(message.getType())) {
            // 保存并设置ID
            Integer scId = saveDanmaku(message, user);
            message.setDanmakuId(scId);
        }

        log.info("礼物/SC处理成功: 用户[{}] 金额[{}]", user.getUsername(), totalCost);
    }

    // 获取榜单
    public List<LeaderboardItemDTO> getLeaderboard(Integer roomId, String type) {
        LocalDateTime startTime;
        LocalDateTime now = LocalDateTime.now();

        switch (type.toUpperCase()) {
            case "SESSION":
                // 获取当前 Session
                Integer sessionId = getCurrentSessionId(roomId);
                if (sessionId == null) return new ArrayList<>();
                List<LeaderboardItemDTO> list = giftDonationRepository.findLeaderboardBySession(sessionId);
                assignRanks(list);
                return list;
            case "DAY":
                startTime = LocalDate.now().atStartOfDay();
                break;
            case "WEEK":
                // 假设周一为第一天
                startTime = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
                break;
            case "MONTH":
                startTime = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Unknown leaderboard type: " + type);
        }

        List<LeaderboardItemDTO> list = giftDonationRepository.findLeaderboardByRoomAndTime(roomId, startTime);
        assignRanks(list);
        return list;
    }

    private void assignRanks(List<LeaderboardItemDTO> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }
    }
}