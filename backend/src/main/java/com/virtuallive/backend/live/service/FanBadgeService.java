package com.virtuallive.backend.live.service;

/**
 * 粉丝牌服务接口
 *
 * 说明：
 * - 每次粉丝在直播间送礼 / 发送 SC 后调用
 * - 根据粉丝对某个主播的历史打赏总额，更新 fan_badges 表中的粉丝牌等级
 *
 * 调用约定：
 * - vtuberId：主播在 users 表中的 user_id
 * - fanId：粉丝在 users 表中的 user_id
 */
public interface FanBadgeService {

    /**
     * 更新粉丝牌等级（如果符合条件则创建或升级）
     *
     * @param vtuberId 主播用户ID
     * @param fanId    粉丝用户ID
     */
    void updateFanBadgeLevel(Integer vtuberId, Integer fanId);

    /**
     * 获取粉丝牌等级
     * @param vtuberId 主播用户ID
     * @param fanId    粉丝用户ID
     * @return 等级 (0表示无牌子)
     */
    Integer getFanBadgeLevel(Integer vtuberId, Integer fanId);
}