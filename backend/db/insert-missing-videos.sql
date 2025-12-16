SET NAMES utf8mb4;
USE virtuallive_dev;

-- Insert missing videos (IDs 4-10)
INSERT IGNORE INTO community_posts (post_id, author_id, title, content, video_url, cover_image_url, created_at, updated_at, is_deleted, views, likes, comments_count) VALUES
(4, 1, '全息角色建模 timelapse', '', 'video-4.mp4', 'cover-4.jpg', NOW(), NOW(), 0, 19000, 0, 0),
(5, 1, '赛博朋克主题竖屏 MV', '', 'video-5.mp4', 'cover-5.jpg', NOW(), NOW(), 0, 46000, 0, 0),
(6, 1, '直播事故剪辑：趣味合集', '', 'video-6.mp4', 'cover-6.jpg', NOW(), NOW(), 0, 62000, 0, 0),
(7, 1, 'AI 虚拟形象调教日常', '', 'video-7.mp4', 'cover-7.jpg', NOW(), NOW(), 0, 37000, 0, 0),
(8, 1, '赛博城市观光 Vlog', '', 'video-8.mp4', 'cover-8.jpg', NOW(), NOW(), 0, 29000, 0, 0),
(9, 1, '虚拟美食节目 · 宇宙餐桌', '', 'video-9.mp4', 'cover-9.jpg', NOW(), NOW(), 0, 42000, 0, 0),
(10, 1, '电竞解说高燃瞬间', '', 'video-10.mp4', 'cover-10.jpg', NOW(), NOW(), 0, 76000, 0, 0);

-- Update titles for ALL videos 1-15 to ensure encoding is correct
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

-- Clear descriptions for all videos 1-15
UPDATE community_posts SET content = '' WHERE post_id BETWEEN 1 AND 15;

SELECT 'Missing videos inserted and titles updated' as status;
