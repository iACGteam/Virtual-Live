USE virtuallive_dev;

-- 1. 允许 session_id 为空 (用于视频弹幕)
ALTER TABLE danmaku MODIFY COLUMN session_id INT NULL;

-- 2. 添加 video_id 列 (如果已存在会报错，可忽略)
-- ALTER TABLE danmaku ADD COLUMN video_id INT NULL AFTER session_id;

-- 3. 添加 video_time 列 (如果已存在会报错，可忽略)
-- ALTER TABLE danmaku ADD COLUMN video_time FLOAT NULL COMMENT 'Video playback time in seconds';

-- 4. 添加索引
-- ALTER TABLE danmaku ADD INDEX idx_video (video_id);

-- 5. 添加外键 (关联到 community_posts 表，即视频表)
-- ALTER TABLE danmaku ADD CONSTRAINT fk_danmaku_video FOREIGN KEY (video_id) REFERENCES community_posts(post_id) ON DELETE CASCADE;
