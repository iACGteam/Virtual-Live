
USE virtuallive_dev;

-- 1. 清空所有视频的简介
UPDATE community_posts SET content = '';

-- 2. 更新现有视频的标题 (IDs 1-10)
UPDATE community_posts SET title = '星海航线直播幕后花絮', category = 'music' WHERE post_id = 1;
UPDATE community_posts SET title = '虚拟偶像舞台 · 夜幕版本', category = 'dance' WHERE post_id = 2;
UPDATE community_posts SET title = '粉丝互动问答高能合集', category = 'game' WHERE post_id = 3;
UPDATE community_posts SET title = '全息角色建模 timelapse', category = 'tech' WHERE post_id = 4;
UPDATE community_posts SET title = '赛博朋克主题竖屏 MV', category = 'music' WHERE post_id = 5;
UPDATE community_posts SET title = '直播事故剪辑：趣味合集', category = 'game' WHERE post_id = 6;
UPDATE community_posts SET title = 'AI 虚拟形象调教日常', category = 'tech' WHERE post_id = 7;
UPDATE community_posts SET title = '赛博城市观光 Vlog', category = 'dance' WHERE post_id = 8;
UPDATE community_posts SET title = '虚拟美食节目 · 宇宙餐桌', category = 'music' WHERE post_id = 9;
UPDATE community_posts SET title = '电竞解说高燃瞬间', category = 'game' WHERE post_id = 10;

-- 3. 插入缺失的视频 (IDs 11-15) 以支持评论功能
-- 注意：这里我们使用默认的 author_id=1 (NebulaNova) 和一些占位数据，主要是为了让 ID 存在
INSERT INTO community_posts (post_id, author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at)
VALUES 
(11, 1, '深夜电台 · 陪伴系列', '', 'music', '虚拟声优', '', '', 0, 0, 33000, 0, NOW()) ON DUPLICATE KEY UPDATE title='深夜电台 · 陪伴系列', content='';

INSERT INTO community_posts (post_id, author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at)
VALUES 
(12, 1, '全息舞狮春节特辑', '', 'dance', '虚拟singer', '', '', 0, 0, 51000, 0, NOW()) ON DUPLICATE KEY UPDATE title='全息舞狮春节特辑', content='';

INSERT INTO community_posts (post_id, author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at)
VALUES 
(13, 1, '音乐制作直播：即时 Remix', '', 'music', '虚拟singer', '', '', 0, 0, 49000, 0, NOW()) ON DUPLICATE KEY UPDATE title='音乐制作直播：即时 Remix', content='';

INSERT INTO community_posts (post_id, author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at)
VALUES 
(14, 1, '虚拟野外求生挑战', '', 'game', '虚拟gamer', '', '', 0, 0, 22000, 0, NOW()) ON DUPLICATE KEY UPDATE title='虚拟野外求生挑战', content='';

INSERT INTO community_posts (post_id, author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at)
VALUES 
(15, 1, '粉丝共创剧情互动剧', '', 'tech', '虚拟声优', '', '', 0, 0, 68000, 0, NOW()) ON DUPLICATE KEY UPDATE title='粉丝共创剧情互动剧', content='';

COMMIT;

SELECT 'Data fixed successfully' as status;
