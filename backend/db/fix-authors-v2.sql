SET NAMES utf8mb4;
USE virtuallive_dev;

-- Create dummy users if they don't exist (IDs 2, 3, 4, 5)
INSERT IGNORE INTO users (user_id, username, password_hash, email, registration_date, user_type, status) VALUES
(2, 'NebulaNova', 'hash123', 'nebula@example.com', NOW(), 'vtuber', 'active'),
(3, 'LumiRay', 'hash123', 'lumi@example.com', NOW(), 'vtuber', 'active'),
(4, 'KiraEcho', 'hash123', 'kira@example.com', NOW(), 'vtuber', 'active'),
(5, 'MoriTech', 'hash123', 'mori@example.com', NOW(), 'vtuber', 'active');

-- Assign authors
UPDATE community_posts SET author_id = 2 WHERE post_id IN (1, 5, 9, 13);
UPDATE community_posts SET author_id = 3 WHERE post_id IN (2, 6, 10, 14);
UPDATE community_posts SET author_id = 4 WHERE post_id IN (3, 7, 11, 15);
UPDATE community_posts SET author_id = 5 WHERE post_id IN (4, 8, 12);

SELECT post_id, title, author_id FROM community_posts WHERE post_id <= 15;
