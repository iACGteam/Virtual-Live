-- VirtuaLive demo data initialization script
-- Usage: mysql -u virtual_live -p123 virtuallive_dev < sample-data.sql

-- 设置客户端字符集为 utf8mb4
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE virtuallive_dev;

-- Clean previous demo fixtures
-- 删除评论数据
DELETE FROM comments WHERE user_id IN (SELECT user_id FROM users WHERE username IN ('NebulaNova', 'LumiRay', 'KiraEcho', 'DemoFan'));
DELETE FROM comments WHERE post_id IN (SELECT post_id FROM community_posts WHERE title LIKE 'Virtua Demo%');

-- 删除帖子数据
DELETE FROM community_posts WHERE title LIKE 'Virtua Demo%';

-- 删除粉丝圈数据
DELETE FROM fan_circles WHERE name IN ('星际航线应援团', '幻光舞社', '回声实验室');

-- 删除用户数据
DELETE FROM users WHERE username IN ('NebulaNova', 'LumiRay', 'KiraEcho', 'DemoFan');

SET FOREIGN_KEY_CHECKS = 1;

-- Insert demo users
INSERT INTO users (username, email, password_hash, phone, user_type, avatar_url, introduction, registration_date, last_login, status)
VALUES
  ('NebulaNova', 'nebula@virtuallive.com', '$2a$10$HwC.wK0NQn6YQz9JIhbSOMG9E8u6KD//Am8YGtSNYGGyRyvE4s46G', NULL, 'vtuber', 'https://cdn.virtuallive.com/avatar/nebula.png', '星际航线的领航员。', '2025-10-01 12:00:00', '2025-11-28 09:30:00', 'active'),
  ('LumiRay', 'lumi@virtuallive.com', '$2a$10$HwC.wK0NQn6YQz9JIhbSOMG9E8u6KD//Am8YGtSNYGGyRyvE4s46G', NULL, 'vtuber', 'https://cdn.virtuallive.com/avatar/lumi.png', '用光谱作画的虚拟舞者。', '2025-10-05 14:20:00', '2025-11-27 20:10:00', 'active'),
  ('KiraEcho', 'kira@virtuallive.com', '$2a$10$HwC.wK0NQn6YQz9JIhbSOMG9E8u6KD//Am8YGtSNYGGyRyvE4s46G', NULL, 'vtuber', 'https://cdn.virtuallive.com/avatar/kira.png', '互动问答的能量体。', '2025-10-12 19:00:00', '2025-11-29 11:00:00', 'active'),
  ('DemoFan', 'fan@virtuallive.com', '$2a$10$HwC.wK0NQn6YQz9JIhbSOMG9E8u6KD//Am8YGtSNYGGyRyvE4s46G', NULL, 'fan', 'https://cdn.virtuallive.com/avatar/fan.png', '资深互动粉丝。', '2025-10-20 08:00:00', '2025-11-30 10:15:00', 'active');

-- Insert fan circles
INSERT INTO fan_circles (name, description, avatar_url, cover_image_url, member_count, post_count, category, is_official, is_active, created_at)
VALUES
  ('星际航线应援团', 'NebulaNova 官方粉丝圈,分享幕后与航行日志。', 'https://cdn.virtuallive.com/circle/nebula-avatar.png', 'https://cdn.virtuallive.com/circle/nebula-cover.jpg', 12840, 560, 'music', 1, 1, '2025-09-12 10:00:00'),
  ('幻光舞社', 'LumiRay 的舞蹈企划与编舞交流社区。', 'https://cdn.virtuallive.com/circle/lumi-avatar.png', 'https://cdn.virtuallive.com/circle/lumi-cover.jpg', 9820, 420, 'dance', 1, 1, '2025-09-20 15:00:00'),
  ('回声实验室', 'KiraEcho 的互动问答&创意活动基地。', 'https://cdn.virtuallive.com/circle/kira-avatar.png', 'https://cdn.virtuallive.com/circle/kira-cover.jpg', 7560, 310, 'game', 0, 1, '2025-10-02 09:30:00');

-- Insert community posts (videos)
INSERT INTO community_posts (author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at, updated_at)
VALUES
  ((SELECT user_id FROM users WHERE username = 'NebulaNova'),
   'Virtua Demo 01 · 星际航线返航现场',
   '全息投影+AI 乐队的返航场景。',
   'music', 'Music,Live,Space',
   'https://cdn.virtuallive.com/video/nebula-cover.jpg',
   'https://cdn.virtuallive.com/video/nebula-demo.mp4',
   1520, 34, 18888, 0,
   '2025-11-18 12:00:00', '2025-11-19 09:00:00'),
  ((SELECT user_id FROM users WHERE username = 'LumiRay'),
   'Virtua Demo 02 · 幻光双轨舞台',
   '两套骨骼同步驱动的双线舞台。',
   'dance', 'Dance,Stage,Live',
   'https://cdn.virtuallive.com/video/lumi-cover.jpg',
   'https://cdn.virtuallive.com/video/lumi-demo.mp4',
   1890, 41, 20560, 0,
   '2025-11-20 18:30:00', '2025-11-21 08:45:00'),
  ((SELECT user_id FROM users WHERE username = 'KiraEcho'),
   'Virtua Demo 03 · 互动问答录播',
   '实时弹幕驱动剧情的问答实验。',
   'game', 'Interactive,Q&A',
   'https://cdn.virtuallive.com/video/kira-cover.jpg',
   'https://cdn.virtuallive.com/video/kira-demo.mp4',
   980, 22, 13220, 0,
   '2025-11-22 15:10:00', '2025-11-22 20:00:00');

-- Insert comments
INSERT INTO comments (post_id, user_id, parent_comment_id, content, likes, created_at, is_deleted)
VALUES
  ((SELECT post_id FROM community_posts WHERE title = 'Virtua Demo 01 · 星际航线返航现场'),
   (SELECT user_id FROM users WHERE username = 'DemoFan'),
   NULL,
   '返航段的能量值拉满!求线下巡回~',
   48,
   '2025-11-19 10:15:00',
   0),
  ((SELECT post_id FROM community_posts WHERE title = 'Virtua Demo 02 · 幻光双轨舞台'),
   (SELECT user_id FROM users WHERE username = 'DemoFan'),
   NULL,
   '骨骼同步太丝滑了,求教程。',
   31,
   '2025-11-21 09:00:00',
   0),
  ((SELECT post_id FROM community_posts WHERE title = 'Virtua Demo 03 · 互动问答录播'),
   (SELECT user_id FROM users WHERE username = 'DemoFan'),
   NULL,
   '剧情分支好玩,再开一场互动直播吧!',
   26,
   '2025-11-23 13:20:00',
   0);

COMMIT;
