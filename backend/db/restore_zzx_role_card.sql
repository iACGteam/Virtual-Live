-- 恢复 'zzx' 用户的角色卡数据
-- 请在数据库工具中运行此脚本，或使用 docker exec 运行

USE virtuallive_dev;

-- 1. 获取 'zzx' 用户的 ID
SET @username = 'zzx';
SET @uid = (SELECT user_id FROM users WHERE username = @username LIMIT 1);

-- 2. 插入角色卡 (如果用户存在)
INSERT INTO role_cards (user_id, name, gender, birthday, height, hobby, status, submit_time, background_story, portrait)
SELECT @uid, 'ZZX', 'male', '2000-06-11', 171, '1', 'approved', NOW(), '恢复的角色卡', ''
WHERE @uid IS NOT NULL;

-- 3. 获取新插入的角色卡 ID
SET @card_id = LAST_INSERT_ID();

-- 4. 插入标签 (根据截图推测或使用默认值)
-- 注意：如果 @card_id 为 0 (即未插入角色卡)，这些插入将失败或无效
INSERT INTO role_card_personality_tags (role_card_id, tag) SELECT @card_id, '傲娇' WHERE @card_id > 0;
INSERT INTO role_card_personality_tags (role_card_id, tag) SELECT @card_id, '温柔' WHERE @card_id > 0;

INSERT INTO role_card_race_tags (role_card_id, tag) SELECT @card_id, '普通人' WHERE @card_id > 0;

INSERT INTO role_card_appearance_tags (role_card_id, tag) SELECT @card_id, '异色瞳' WHERE @card_id > 0;

SELECT CONCAT('已尝试恢复用户 ', @username, ' 的角色卡。如果用户不存在，则未执行任何操作。') as result;
