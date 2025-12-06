-- 创建粉丝与社区系统所需的表
USE virtuallive_dev;

-- 1. 创建点赞表
CREATE TABLE IF NOT EXISTS likes (
    like_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    content_type ENUM('post', 'comment', 'asset') NOT NULL,
    content_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_content (user_id, content_type, content_id),
    INDEX idx_content (content_type, content_id),
    UNIQUE KEY unique_like (user_id, content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 创建收藏表
CREATE TABLE IF NOT EXISTS favorites (
    favorite_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    content_type ENUM('post', 'asset') NOT NULL,
    content_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_content (user_id, content_type, content_id),
    INDEX idx_user_created (user_id, created_at),
    UNIQUE KEY unique_favorite (user_id, content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. user_follows table
CREATE TABLE IF NOT EXISTS user_follows (
    follow_id INT PRIMARY KEY AUTO_INCREMENT,
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    follow_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id),
    UNIQUE KEY unique_follow (follower_id, following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. circle_members table
CREATE TABLE IF NOT EXISTS circle_members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    circle_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (circle_id) REFERENCES fan_circles(circle_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_circle (circle_id),
    INDEX idx_user (user_id),
    INDEX idx_joined_at (joined_at),
    UNIQUE KEY unique_circle_member (circle_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. daily_checkins table
CREATE TABLE IF NOT EXISTS daily_checkins (
    checkin_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    circle_id INT NOT NULL,
    checkin_date DATE NOT NULL,
    continuous_days INT DEFAULT 1,
    total_days INT DEFAULT 1,
    reward_points INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (circle_id) REFERENCES fan_circles(circle_id) ON DELETE CASCADE,
    INDEX idx_user_circle (user_id, circle_id),
    INDEX idx_checkin_date (checkin_date),
    UNIQUE KEY unique_user_circle_date (user_id, circle_id, checkin_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Add circle_id to community_posts
ALTER TABLE community_posts 
ADD COLUMN circle_id INT NULL,
ADD INDEX idx_circle_id (circle_id);

COMMIT;

-- 输出创建结果
SELECT 'Tables created successfully for community and fan system' AS status;
