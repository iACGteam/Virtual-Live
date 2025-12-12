-- VirtuaLive Demo Data Insertion Script
-- Run this after database initialization
-- Usage: Get-Content backend/db/insert-demo-data.sql -Encoding UTF8 -Raw | docker exec -i virtuallive-mysql-dev mysql -u virtual_live -p123 virtuallive_dev

SET NAMES utf8mb4;
USE virtuallive_dev;

-- Clean existing demo data
DELETE FROM comments WHERE comment_id < 1000;
DELETE FROM community_posts WHERE post_id < 1000;
DELETE FROM fan_circles WHERE circle_id < 1000;
DELETE FROM users WHERE user_id < 1000;

-- Insert demo users
INSERT INTO users (user_id, username, email, password_hash, user_type, avatar_url, introduction, registration_date, status) VALUES
(1, 'NebulaNova', 'nebula@demo.com', '$2a$10$demoHashPassword123456789012345678901234567890', 'vtuber', 'https://i.pravatar.cc/150?img=1', '星际航线的领航员，探索宇宙的奥秘', '2024-10-01 12:00:00', 'active'),
(2, 'LumiRay', 'lumi@demo.com', '$2a$10$demoHashPassword123456789012345678901234567890', 'vtuber', 'https://i.pravatar.cc/150?img=5', '用光谱作画的虚拟舞者', '2024-10-05 14:20:00', 'active'),
(3, 'KiraEcho', 'kira@demo.com', '$2a$10$demoHashPassword123456789012345678901234567890', 'vtuber', 'https://i.pravatar.cc/150?img=9', '互动问答的能量体', '2024-10-12 19:00:00', 'active'),
(4, 'TechMaster', 'tech@demo.com', '$2a$10$demoHashPassword123456789012345678901234567890', 'creator', 'https://i.pravatar.cc/150?img=12', '科技区UP主，分享最新技术', '2024-11-01 10:00:00', 'active'),
(5, 'DemoFan', 'fan@demo.com', '$2a$10$demoHashPassword123456789012345678901234567890', 'fan', 'https://i.pravatar.cc/150?img=20', '资深粉丝，热爱二次元文化', '2024-10-20 08:00:00', 'active');

-- Insert fan circles
INSERT INTO fan_circles (circle_id, name, description, avatar_url, cover_image_url, member_count, post_count, category, is_official, is_active, created_at) VALUES
(1, '星际航线应援团', 'NebulaNova官方粉丝圈，一起探索星辰大海', 'https://i.pravatar.cc/200?img=1', 'https://picsum.photos/seed/circle1/800/400', 12840, 560, 'music', 1, 1, '2024-09-12 10:00:00'),
(2, '幻光舞社', 'LumiRay的舞蹈企划与编舞交流社区', 'https://i.pravatar.cc/200?img=5', 'https://picsum.photos/seed/circle2/800/400', 9820, 420, 'dance', 1, 1, '2024-09-20 15:00:00'),
(3, '回声实验室', 'KiraEcho的互动问答与创意活动基地', 'https://i.pravatar.cc/200?img=9', 'https://picsum.photos/seed/circle3/800/400', 7560, 310, 'game', 0, 1, '2024-10-02 09:30:00'),
(4, '科技前沿讨论组', '分享最新科技资讯和技术交流', 'https://i.pravatar.cc/200?img=12', 'https://picsum.photos/seed/circle4/800/400', 5240, 280, 'tech', 0, 1, '2024-10-15 14:00:00'),
(5, '美食探索频道', '发现美食，分享烹饪技巧', 'https://i.pravatar.cc/200?img=15', 'https://picsum.photos/seed/circle5/800/400', 4180, 195, 'food', 0, 1, '2024-11-01 11:00:00');

-- Insert community posts (videos) - using open source video URLs
INSERT INTO community_posts (post_id, author_id, title, content, category, tags, cover_image_url, video_url, likes, comments_count, views, is_deleted, created_at) VALUES
(1, 1, '星际航线返航现场 - 全息投影演唱会', '这是一场震撼的全息投影演唱会，AI乐队现场伴奏，带你领略星空之美。使用最新的虚拟技术打造沉浸式体验。', 'music', 'Music,Live,Space', 'https://picsum.photos/seed/video1/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 1520, 34, 18888, 0, '2024-11-18 12:00:00'),
(2, 2, '幻光双轨舞台 - 同步舞蹈表演', '两套骨骼同步驱动的双线舞台演出，展示虚拟舞者的极致魅力。融合现代舞和传统元素的创新编舞。', 'dance', 'Dance,Stage,Live', 'https://picsum.photos/seed/video2/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 1890, 41, 20560, 0, '2024-11-20 18:30:00'),
(3, 3, '互动问答实验 - 弹幕驱动剧情', '实时弹幕驱动剧情发展的互动实验，你的每一条弹幕都会影响故事走向！', 'game', 'Interactive,Q&A,Game', 'https://picsum.photos/seed/video3/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 980, 22, 13220, 0, '2024-11-22 15:10:00'),
(4, 4, 'AI技术解析 - 虚拟主播的背后', '深度解析虚拟主播使用的AI技术，从动作捕捉到实时渲染的全流程讲解。', 'tech', 'AI,Tech,Tutorial', 'https://picsum.photos/seed/video4/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 2340, 67, 28900, 0, '2024-11-25 10:20:00'),
(5, 1, '星空下的音乐会 - 钢琴独奏', '在虚拟星空下的钢琴演奏，带来宁静而美好的夜晚。', 'music', 'Music,Piano,Chill', 'https://picsum.photos/seed/video5/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 1120, 28, 15670, 0, '2024-11-26 20:00:00'),
(6, 2, '舞蹈教学 - 基础动作分解', '适合新手的舞蹈基础教学，从零开始学习虚拟舞蹈动作。', 'dance', 'Dance,Tutorial,Beginner', 'https://picsum.photos/seed/video6/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4', 890, 19, 12450, 0, '2024-11-27 14:30:00'),
(7, 3, '游戏直播回放 - 恐怖游戏挑战', '深夜恐怖游戏挑战，和粉丝一起经历惊险刺激的冒险！', 'game', 'Game,Horror,Stream', 'https://picsum.photos/seed/video7/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4', 1650, 45, 19230, 0, '2024-11-28 22:00:00'),
(8, 4, '虚拟形象制作教程 - Live2D篇', '从零开始制作Live2D虚拟形象，详细步骤全记录。', 'tech', 'Tech,Live2D,Tutorial', 'https://picsum.photos/seed/video8/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4', 2890, 78, 35600, 0, '2024-11-29 16:00:00'),
(9, 1, '翻唱合集 - 经典动漫歌曲', '精选经典动漫歌曲翻唱合集，回忆满满的旋律。', 'music', 'Music,Cover,Anime', 'https://picsum.photos/seed/video9/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4', 1780, 52, 22340, 0, '2024-11-30 19:00:00'),
(10, 2, '年度舞台回顾 - 精彩瞬间', '回顾今年所有精彩的舞台表演瞬间，感谢大家的支持！', 'dance', 'Dance,Review,Highlight', 'https://picsum.photos/seed/video10/800/450', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4', 3120, 89, 41200, 0, '2024-12-01 12:00:00');

-- Insert comments
INSERT INTO comments (comment_id, post_id, user_id, content, likes, created_at, is_deleted) VALUES
(1, 1, 5, '太震撼了！全息投影效果简直绝了！', 48, '2024-11-19 10:15:00', 0),
(2, 1, 4, '音乐和视觉效果完美结合，期待下次演出！', 32, '2024-11-19 11:30:00', 0),
(3, 2, 5, '舞蹈动作好流畅，双人同步太厉害了！', 31, '2024-11-21 09:00:00', 0),
(4, 2, 3, '编舞很有创意，学到了很多！', 25, '2024-11-21 10:20:00', 0),
(5, 3, 5, '互动体验太好玩了，下次还要参加！', 26, '2024-11-23 13:20:00', 0),
(6, 3, 1, '谢谢大家的参与，之后会做更多互动企划！', 45, '2024-11-23 14:00:00', 0),
(7, 4, 5, '讲解得很详细，作为初学者表示收获很大！', 67, '2024-11-26 08:30:00', 0),
(8, 4, 2, '技术细节满满，期待后续更新！', 42, '2024-11-26 09:45:00', 0),
(9, 5, 5, '钢琴声音好治愈，循环播放中～', 38, '2024-11-27 21:30:00', 0),
(10, 6, 5, '跟着视频学了好久，终于学会基础动作了！', 29, '2024-11-28 16:00:00', 0),
(11, 7, 5, '看得我心跳加速，但又停不下来哈哈', 52, '2024-11-29 23:15:00', 0),
(12, 8, 5, '教程太详细了，马上开始制作自己的形象！', 88, '2024-11-30 18:00:00', 0),
(13, 9, 5, '满满的回忆杀，每首都好听！', 61, '2024-12-01 20:30:00', 0),
(14, 10, 5, '今年的舞台都好精彩，明年继续加油！', 95, '2024-12-02 13:00:00', 0);

COMMIT;

SELECT '=== Demo Data Inserted Successfully ===' AS status;
SELECT CONCAT('Users: ', COUNT(*)) AS count FROM users WHERE user_id < 1000;
SELECT CONCAT('Circles: ', COUNT(*)) AS count FROM fan_circles WHERE circle_id < 1000;
SELECT CONCAT('Videos: ', COUNT(*)) AS count FROM community_posts WHERE post_id < 1000;
SELECT CONCAT('Comments: ', COUNT(*)) AS count FROM comments WHERE comment_id < 1000;
