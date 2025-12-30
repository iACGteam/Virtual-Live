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
        // 将等级拓展到 Lv1 ~ Lv40，使用阶梯型门槛（单位：元）
        private static final double[] LEVEL_THRESHOLDS = new double[] {
            1, 3, 5, 10, 30, 100, 300, 500, 1000, 2000,
            3000, 5000, 8000, 10000, 15000, 20000, 30000, 50000, 80000, 100000,
            150000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000,
            1250000, 1500000, 2000000, 2500000, 3000000, 4000000, 5000000, 7000000, 10000000, 15000000
        };

    @Override
    public void updateFanBadgeLevel(Integer vtuberId, Integer fanId) {
        // 防御性判断：ID 为空直接返回（不抛异常，避免影响主流程）
        if (vtuberId == null || fanId == null) {
            return;
        }

        // 注意：即使用户当前未关注主播，也需要记录其对该主播的打赏累计（用于后续关注后赋予等级）

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
        String badgeName = "Lv." + level + " 粉丝";

        // 明确使用存在性检查后再执行 INSERT 或 UPDATE，避免依赖数据库必须存在的唯一索引
        try {
            String existsSql = "SELECT COUNT(*) FROM fan_badges WHERE vtuber_id = ? AND fan_id = ?";
            Integer cnt = jdbcTemplate.queryForObject(existsSql, Integer.class, vtuberId, fanId);
            if (cnt != null && cnt > 0) {
                String updateSql = "UPDATE fan_badges SET badge_level = ?, badge_name = ?, acquired_date = CURRENT_TIMESTAMP WHERE vtuber_id = ? AND fan_id = ?";
                jdbcTemplate.update(updateSql, level, badgeName, vtuberId, fanId);
            } else {
                String insertSql = "INSERT INTO fan_badges (vtuber_id, fan_id, badge_level, badge_name, acquired_date) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
                jdbcTemplate.update(insertSql, vtuberId, fanId, level, badgeName);
            }
        } catch (Exception e) {
            log.error("写入 fan_badges 失败", e);
        }

        log.info("更新粉丝牌成功（记录等级信息）：vtuberId={}, fanId={}, totalAmount={}, level={}",
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