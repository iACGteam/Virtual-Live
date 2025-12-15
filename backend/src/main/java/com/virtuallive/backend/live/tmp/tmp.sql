-- 让粉丝 88 关注主播 11
INSERT INTO user_follows (follower_id, following_id)
VALUES (88, 11)
    ON DUPLICATE KEY UPDATE is_blocked = 0;

-- 清掉旧粉丝牌（方便看变化）
DELETE FROM fan_badges WHERE vtuber_id = 11 AND fan_id = 88;

-- 每次送完礼，在数据库里查粉丝牌
SELECT * FROM fan_badges WHERE vtuber_id = 11 AND fan_id = 88;