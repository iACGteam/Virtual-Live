-- 不会破坏现有数据，只是保证同一主播 + 同一粉丝只有一条粉丝牌记录。只需要执行一次，建议在测试库先试
ALTER TABLE fan_badges
    ADD UNIQUE KEY uniq_vtuber_fan (vtuber_id, fan_id);