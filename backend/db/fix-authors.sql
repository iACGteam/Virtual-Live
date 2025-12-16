SET NAMES utf8mb4;
USE virtuallive_dev;

-- Randomize authors for videos 1-15 to simulate a real community
-- User IDs: 1, 2, 3 (assuming these exist, if not, they will default to 1 or fail if FK constraint, but usually we have a few users)
-- If only user 1 exists, we might need to create more users. Let's check users first.

-- Create dummy users if they don't exist (IDs 2, 3, 4, 5)
INSERT IGNORE INTO users (user_id, username, password, email, created_at, updated_at) VALUES
(2, 'NebulaNova', 'password', 'nebula@example.com', NOW(), NOW()),
(3, 'LumiRay', 'password', 'lumi@example.com', NOW(), NOW()),
(4, 'KiraEcho', 'password', 'kira@example.com', NOW(), NOW()),
(5, 'MoriTech', 'password', 'mori@example.com', NOW(), NOW());

-- Assign authors
UPDATE community_posts SET author_id = 2 WHERE post_id IN (1, 5, 9, 13);
UPDATE community_posts SET author_id = 3 WHERE post_id IN (2, 6, 10, 14);
UPDATE community_posts SET author_id = 4 WHERE post_id IN (3, 7, 11, 15);
UPDATE community_posts SET author_id = 5 WHERE post_id IN (4, 8, 12);

SELECT post_id, title, author_id FROM community_posts WHERE post_id <= 15;
