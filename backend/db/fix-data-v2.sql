
SET NAMES utf8mb4;
USE virtuallive_dev;

-- 1. 修复视频标题 (再次执行，确保编码正确)
UPDATE community_posts SET title = '星海航线直播幕后花絮' WHERE post_id = 1;
UPDATE community_posts SET title = '虚拟偶像舞台 · 夜幕版本' WHERE post_id = 2;
UPDATE community_posts SET title = '粉丝互动问答高能合集' WHERE post_id = 3;
UPDATE community_posts SET title = '全息角色建模 timelapse' WHERE post_id = 4;
UPDATE community_posts SET title = '赛博朋克主题竖屏 MV' WHERE post_id = 5;
UPDATE community_posts SET title = '直播事故剪辑：趣味合集' WHERE post_id = 6;
UPDATE community_posts SET title = 'AI 虚拟形象调教日常' WHERE post_id = 7;
UPDATE community_posts SET title = '赛博城市观光 Vlog' WHERE post_id = 8;
UPDATE community_posts SET title = '虚拟美食节目 · 宇宙餐桌' WHERE post_id = 9;
UPDATE community_posts SET title = '电竞解说高燃瞬间' WHERE post_id = 10;
UPDATE community_posts SET title = '深夜电台 · 陪伴系列' WHERE post_id = 11;
UPDATE community_posts SET title = '全息舞狮春节特辑' WHERE post_id = 12;
UPDATE community_posts SET title = '音乐制作直播：即时 Remix' WHERE post_id = 13;
UPDATE community_posts SET title = '虚拟野外求生挑战' WHERE post_id = 14;
UPDATE community_posts SET title = '粉丝共创剧情互动剧' WHERE post_id = 15;

-- 2. 同步评论数量
-- 将 community_posts 表中的 comments_count 字段更新为 comments 表中的实际评论数
UPDATE community_posts p
SET comments_count = (
    SELECT COUNT(*)
    FROM comments c
    WHERE c.post_id = p.post_id
    AND c.is_deleted = 0
);

SELECT 'Titles fixed and comment counts synchronized' as status;
