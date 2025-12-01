-- Create missing tables for VirtuaLive backend

USE virtuallive_dev;

-- Create fan_circles table
CREATE TABLE IF NOT EXISTS fan_circles (
    circle_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    avatar_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    member_count INT DEFAULT 0,
    post_count INT DEFAULT 0,
    category VARCHAR(50),
    is_official BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_active (is_active),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add video_url column to community_posts (ignore error if exists)
ALTER TABLE community_posts 
ADD COLUMN video_url VARCHAR(500) AFTER cover_image_url;

COMMIT;
