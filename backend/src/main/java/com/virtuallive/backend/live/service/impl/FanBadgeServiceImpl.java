package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.service.FanBadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 粉丝牌服务实现类
 *
 * 主要功能：
 * 1. 判断粉丝是否已经关注该主播（user_follows 表）
 * 2. 统计粉丝对该主播的累计打赏金额（gift_donations + live_sessions + live_rooms）
 * 3. 根据金额计算粉丝牌等级（1~30级）
 * 4. 写入 / 更新 fan_badges 表（使用 INSERT ... ON DUPLICATE KEY UPDATE）
 *
 * 注意：
 * - 这是新增的逻辑，不会改变你现有的直播、礼物等主流程
 * - 在礼物处理逻辑里调用即可（建议 try-catch 包一下，以免出错影响主流程）
 *
 * ★ 数据库准备（只做一次）：
 *   建议在数据库里为 fan_badges 表加一个联合唯一索引：
 *
 *   ALTER TABLE fan_badges
 *   ADD UNIQUE KEY uniq_vtuber_fan (vtuber_id, fan_id);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FanBadgeServiceImpl implements FanBadgeService {

    /**
     * 使用 Spring 的 JdbcTemplate 直接执行 SQL
     * 这样可以少写 Repository，不动你们现有结构。
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * 等级门槛数组
     * - index = 0 对应 Lv.1，index = 29 对应 Lv.30
     * - 值为“累计打赏金额”的最低要求（单位：元）
     *
     * 规则总结（total = 针对同一个主播的总打赏金额）：
     *
     * 这里不再有“只要关注就送 Lv.1”，
     * 所有等级都需要一定的打赏金额：
     *
     * Lv.1 ：total ≥ 1
     * Lv.2 ：total ≥ 3
     * Lv.3 ：total ≥ 5
     * Lv.4 ：total ≥ 10
     * Lv.5 ：total ≥ 30
     * Lv.6 ：total ≥ 100
     * Lv.7 ：total ≥ 300
     * Lv.8 ：total ≥ 500
     * Lv.9 ：total ≥ 1,000
     * Lv.10：total ≥ 2,000
     * Lv.11：total ≥ 3,000
     * Lv.12：total ≥ 5,000
     * Lv.13：total ≥ 8,000
     * Lv.14：total ≥ 10,000
     * Lv.15：total ≥ 15,000
     * Lv.16：total ≥ 20,000
     * Lv.17：total ≥ 30,000
     * Lv.18：total ≥ 50,000
     * Lv.19：total ≥ 80,000
     * Lv.20：total ≥ 100,000
     * Lv.21：total ≥ 150,000
     * Lv.22：total ≥ 200,000
     * Lv.23：total ≥ 300,000
     * Lv.24：total ≥ 400,000
     * Lv.25：total ≥ 500,000
     * Lv.26：total ≥ 600,000
     * Lv.27：total ≥ 700,000
     * Lv.28：total ≥ 800,000
     * Lv.29：total ≥ 900,000
     * Lv.30：total ≥ 1,000,000
     */
    private static final double[] LEVEL_THRESHOLDS = new double[] {
            1,        // Lv.1：≥ 1 元
            3,        // Lv.2：≥ 3 元
            5,        // Lv.3：≥ 5 元
            10,       // Lv.4：≥ 10 元
            30,       // Lv.5：≥ 30 元
            100,      // Lv.6：≥ 100 元
            300,      // Lv.7：≥ 300 元
            500,      // Lv.8：≥ 500 元
            1000,     // Lv.9：≥ 1000 元
            2000,     // Lv.10：≥ 2000 元
            3000,     // Lv.11：≥ 3000 元
            5000,     // Lv.12：≥ 5000 元
            8000,     // Lv.13：≥ 8000 元
            10000,    // Lv.14：≥ 10000 元
            15000,    // Lv.15：≥ 15000 元
            20000,    // Lv.16：≥ 20000 元
            30000,    // Lv.17：≥ 30000 元
            50000,    // Lv.18：≥ 50000 元
            80000,    // Lv.19：≥ 80000 元
            100000,   // Lv.20：≥ 100000 元
            150000,   // Lv.21：≥ 150000 元
            200000,   // Lv.22：≥ 200000 元
            300000,   // Lv.23：≥ 300000 元
            400000,   // Lv.24：≥ 400000 元
            500000,   // Lv.25：≥ 500000 元
            600000,   // Lv.26：≥ 600000 元
            700000,   // Lv.27：≥ 700000 元
            800000,   // Lv.28：≥ 800000 元
            900000,   // Lv.29：≥ 900000 元
            1000000   // Lv.30：≥ 1000000 元
    };

    @Override
    public void updateFanBadgeLevel(Integer vtuberId, Integer fanId) {
        // 防御性判断：ID 为空直接返回（不抛异常，避免影响主流程）
        if (vtuberId == null || fanId == null) {
            return;
        }

        // 0. 必须“已经关注”了该主播，才会有粉丝牌
        boolean followed = isFollowed(vtuberId, fanId);
        if (!followed) {
            // 如果你想“打赏时自动关注”，可以在这里插入一条 user_follows 记录
            // 这里为了不改变现有逻辑，先简单返回
            log.debug("粉丝未关注主播，不更新粉丝牌：vtuberId={}, fanId={}", vtuberId, fanId);
            return;
        }

        // 1. 计算粉丝对该主播的累计打赏金额（单位：元）
        BigDecimal totalAmount = getTotalDonationAmount(vtuberId, fanId);
        // 保险：避免 null
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        // 2. 根据金额计算等级（1~30级）
        int level = calculateLevel(totalAmount);
        if (level <= 0) {
            return;
        }

        // 3. 写入 fan_badges 表（有则更新，无则插入）
        // 需要保证 fan_badges 上有唯一索引 (vtuber_id, fan_id)：
        // ALTER TABLE fan_badges
        // ADD UNIQUE KEY uniq_vtuber_fan (vtuber_id, fan_id);
        String upsertSql = """
                INSERT INTO fan_badges (vtuber_id, fan_id, badge_level, badge_name)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    badge_level = VALUES(badge_level),
                    badge_name  = VALUES(badge_name)
                """;

        String badgeName = "Lv." + level + " 粉丝";

        jdbcTemplate.update(upsertSql, vtuberId, fanId, level, badgeName);

        log.info("更新粉丝牌成功：vtuberId={}, fanId={}, totalAmount={}, level={}",
                vtuberId, fanId, totalAmount, level);
    }

    /**
     * 判断“粉丝是否已经关注该主播”
     *
     * 对应 user_follows 表：
     * follower_id = 粉丝
     * following_id = 被关注的人（主播）
     */
    private boolean isFollowed(Integer vtuberId, Integer fanId) {
        String sql = """
                SELECT COUNT(*)
                FROM user_follows
                WHERE follower_id = ? AND following_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, fanId, vtuberId);
        return count != null && count > 0;
    }

    /**
     * 计算粉丝对某个主播的“历史总打赏金额”
     *
     * 逻辑：
     * - 从 gift_donations 开始
     * - 关联 live_sessions -> live_rooms -> vtuber_id
     */
    private BigDecimal getTotalDonationAmount(Integer vtuberId, Integer fanId) {
        String sql = """
                SELECT COALESCE(SUM(gd.total_value), 0) AS total_amount
                FROM gift_donations gd
                JOIN live_sessions ls ON gd.session_id = ls.session_id
                JOIN live_rooms lr ON ls.room_id = lr.room_id
                WHERE lr.vtuber_id = ? AND gd.sender_id = ?
                """;

        return jdbcTemplate.queryForObject(sql, BigDecimal.class, vtuberId, fanId);
    }

    /**
     * 根据累计金额计算粉丝牌等级（1~30级）
     *
     * 实现方式：
     * - 使用 LEVEL_THRESHOLDS 数组，从最高等级往下查
     * - 找到“总金额 >= 对应门槛”的最高等级
     */
    private int calculateLevel(BigDecimal totalAmount) {
        double total = totalAmount.doubleValue();
        int level = 1; // 默认至少 1 级，只要有打赏且满足门槛

        // 从最高等级往下找（这样更快）
        for (int i = LEVEL_THRESHOLDS.length - 1; i >= 0; i--) {
            if (total >= LEVEL_THRESHOLDS[i]) {
                level = i + 1; // 数组下标 0 对应 Lv.1
                break;
            }
        }

        // 双保险，限制在 1~30 之间
        if (level < 1) level = 1;
        if (level > LEVEL_THRESHOLDS.length) level = LEVEL_THRESHOLDS.length;
        return level;
    }

    @Override
    public Integer getFanBadgeLevel(Integer vtuberId, Integer fanId) {
        String sql = "SELECT badge_level FROM fan_badges WHERE vtuber_id = ? AND fan_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, vtuberId, fanId);
        } catch (Exception e) {
            return 0; // 无牌子
        }
    }
}