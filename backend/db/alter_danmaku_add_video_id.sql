ALTER TABLE danmaku MODIFY COLUMN session_id INT NULL;
ALTER TABLE danmaku ADD COLUMN video_id INT NULL AFTER session_id;
ALTER TABLE danmaku ADD INDEX idx_video (video_id);
ALTER TABLE danmaku ADD CONSTRAINT fk_danmaku_video FOREIGN KEY (video_id) REFERENCES community_posts(post_id) ON DELETE CASCADE;
