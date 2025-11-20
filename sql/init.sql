-- 创建数据库
CREATE DATABASE IF NOT EXISTS virtuallive_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE virtuallive_dev;

-- 用户表
CREATE TABLE users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  user_type ENUM('vtuber', 'creator', 'fan', 'merchant') NOT NULL,
  avatar_url VARCHAR(500),
  introduction TEXT,
  registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP,
  status ENUM('active', 'banned', 'inactive') DEFAULT 'active',
  INDEX idx_username (username),
  INDEX idx_email (email),
  INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户资金账户
CREATE TABLE user_wallet (
  wallet_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL UNIQUE,
  balance DECIMAL(12,2) DEFAULT 0,
  total_earned DECIMAL(12,2) DEFAULT 0,
  total_spent DECIMAL(12,2) DEFAULT 0,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户关注关系
CREATE TABLE user_follows (
  follow_id INT PRIMARY KEY AUTO_INCREMENT,
  follower_id INT NOT NULL,
  following_id INT NOT NULL,
  follow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_blocked BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE,
  UNIQUE KEY unique_follow (follower_id, following_id),
  INDEX idx_follower (follower_id),
  INDEX idx_following (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 粉丝勋章
CREATE TABLE fan_badges (
  badge_id INT PRIMARY KEY AUTO_INCREMENT,
  vtuber_id INT NOT NULL,
  fan_id INT NOT NULL,
  badge_level INT DEFAULT 1,
  badge_name VARCHAR(100),
  acquired_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (vtuber_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (fan_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_vtuber (vtuber_id),
  INDEX idx_fan (fan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 直播间
CREATE TABLE live_rooms (
  room_id INT PRIMARY KEY AUTO_INCREMENT,
  vtuber_id INT NOT NULL,
  room_title VARCHAR(200) NOT NULL,
  description TEXT,
  category VARCHAR(50),
  thumbnail_url VARCHAR(500),
  is_live BOOLEAN DEFAULT FALSE,
  stream_key VARCHAR(255) UNIQUE,
  rtmp_server VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (vtuber_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_vtuber (vtuber_id),
  INDEX idx_is_live (is_live)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 直播会话
CREATE TABLE live_sessions (
  session_id INT PRIMARY KEY AUTO_INCREMENT,
  room_id INT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP,
  duration_minutes INT,
  viewer_count INT DEFAULT 0,
  peak_viewers INT DEFAULT 0,
  total_gifts_revenue DECIMAL(12,2) DEFAULT 0,
  FOREIGN KEY (room_id) REFERENCES live_rooms(room_id) ON DELETE CASCADE,
  INDEX idx_room (room_id),
  INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 弹幕记录
CREATE TABLE danmaku (
  danmaku_id INT PRIMARY KEY AUTO_INCREMENT,
  session_id INT NOT NULL,
  user_id INT NOT NULL,
  content TEXT NOT NULL,
  color VARCHAR(7) DEFAULT '#FFFFFF',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (session_id) REFERENCES live_sessions(session_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_session (session_id),
  INDEX idx_user (user_id),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 礼物打赏
CREATE TABLE gift_donations (
  donation_id INT PRIMARY KEY AUTO_INCREMENT,
  session_id INT NOT NULL,
  sender_id INT NOT NULL,
  gift_type VARCHAR(100) NOT NULL,
  gift_name VARCHAR(100),
  gift_value DECIMAL(12,2) NOT NULL,
  quantity INT DEFAULT 1,
  total_value DECIMAL(12,2) NOT NULL,
  donation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES live_sessions(session_id) ON DELETE CASCADE,
  FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_session (session_id),
  INDEX idx_sender (sender_id),
  INDEX idx_donation_time (donation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 直播回放
CREATE TABLE live_playbacks (
  playback_id INT PRIMARY KEY AUTO_INCREMENT,
  session_id INT NOT NULL,
  video_url VARCHAR(500),
  thumbnail_url VARCHAR(500),
  duration_minutes INT,
  size_mb INT,
  status ENUM('processing', 'available', 'deleted') DEFAULT 'processing',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES live_sessions(session_id) ON DELETE CASCADE,
  INDEX idx_session (session_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 观看历史
CREATE TABLE view_history (
  view_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  session_id INT NOT NULL,
  watch_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  watch_duration_minutes INT,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (session_id) REFERENCES live_sessions(session_id) ON DELETE CASCADE,
  INDEX idx_user_session (user_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 虚拟资产类型
CREATE TABLE asset_types (
  type_id INT PRIMARY KEY AUTO_INCREMENT,
  type_name VARCHAR(100) NOT NULL UNIQUE,
  description TEXT,
  commission_rate DECIMAL(5,2) DEFAULT 20,
  INDEX idx_type_name (type_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 虚拟资产
CREATE TABLE virtual_assets (
  asset_id INT PRIMARY KEY AUTO_INCREMENT,
  creator_id INT NOT NULL,
  asset_type_id INT NOT NULL,
  asset_name VARCHAR(200) NOT NULL,
  description TEXT,
  preview_image_url VARCHAR(500),
  tags VARCHAR(200),
  price DECIMAL(12,2),
  free_download BOOLEAN DEFAULT FALSE,
  status ENUM('draft', 'pending_review', 'approved', 'rejected', 'delisted') DEFAULT 'draft',
  download_count INT DEFAULT 0,
  purchase_count INT DEFAULT 0,
  rating DECIMAL(3,2) DEFAULT 0,
  rating_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (asset_type_id) REFERENCES asset_types(type_id),
  INDEX idx_creator (creator_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at),
  INDEX idx_asset_type (asset_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 资产版本管理
CREATE TABLE asset_versions (
  version_id INT PRIMARY KEY AUTO_INCREMENT,
  asset_id INT NOT NULL,
  version_number VARCHAR(50),
  file_url VARCHAR(500),
  file_size_mb INT,
  update_notes TEXT,
  release_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_latest BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (asset_id) REFERENCES virtual_assets(asset_id) ON DELETE CASCADE,
  INDEX idx_asset (asset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 资产购买记录
CREATE TABLE asset_purchases (
  purchase_id INT PRIMARY KEY AUTO_INCREMENT,
  asset_id INT NOT NULL,
  buyer_id INT NOT NULL,
  price_paid DECIMAL(12,2),
  purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  transaction_id VARCHAR(100) UNIQUE,
  download_link VARCHAR(500),
  is_refunded BOOLEAN DEFAULT FALSE,
  refund_date TIMESTAMP,
  FOREIGN KEY (asset_id) REFERENCES virtual_assets(asset_id) ON DELETE CASCADE,
  FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_buyer (buyer_id),
  INDEX idx_asset (asset_id),
  INDEX idx_purchase_date (purchase_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 资产评论与评分
CREATE TABLE asset_reviews (
  review_id INT PRIMARY KEY AUTO_INCREMENT,
  asset_id INT NOT NULL,
  reviewer_id INT NOT NULL,
  rating INT CHECK (rating >= 1 AND rating <= 5),
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  helpful_count INT DEFAULT 0,
  FOREIGN KEY (asset_id) REFERENCES virtual_assets(asset_id) ON DELETE CASCADE,
  FOREIGN KEY (reviewer_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_asset (asset_id),
  INDEX idx_reviewer (reviewer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创作者收益记录
CREATE TABLE creator_revenue (
  revenue_id INT PRIMARY KEY AUTO_INCREMENT,
  creator_id INT NOT NULL,
  asset_id INT,
  revenue_type ENUM('asset_sale', 'commission', 'tip') NOT NULL,
  amount DECIMAL(12,2),
  commission_rate DECIMAL(5,2),
  revenue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  settlement_status ENUM('pending', 'settled', 'failed') DEFAULT 'pending',
  FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (asset_id) REFERENCES virtual_assets(asset_id) ON DELETE SET NULL,
  INDEX idx_creator (creator_id),
  INDEX idx_revenue_date (revenue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 社区投稿
CREATE TABLE community_posts (
  post_id INT PRIMARY KEY AUTO_INCREMENT,
  author_id INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content LONGTEXT NOT NULL,
  category VARCHAR(100),
  tags VARCHAR(200),
  cover_image_url VARCHAR(500),
  likes INT DEFAULT 0,
  comments_count INT DEFAULT 0,
  views INT DEFAULT 0,
  is_deleted BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_author (author_id),
  INDEX idx_category (category),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 评论
CREATE TABLE comments (
  comment_id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT,
  user_id INT NOT NULL,
  parent_comment_id INT,
  content TEXT NOT NULL,
  likes INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (post_id) REFERENCES community_posts(post_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
  INDEX idx_post (post_id),
  INDEX idx_user (user_id),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 点赞
CREATE TABLE likes (
  like_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  content_type ENUM('post', 'comment', 'asset') NOT NULL,
  content_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  UNIQUE KEY unique_like (user_id, content_type, content_id),
  INDEX idx_content (content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 收藏
CREATE TABLE favorites (
  favorite_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  content_type ENUM('post', 'asset', 'vtuber') NOT NULL,
  content_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  UNIQUE KEY unique_favorite (user_id, content_type, content_id),
  INDEX idx_user_type (user_id, content_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 话题讨论区
CREATE TABLE discussion_topics (
  topic_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  creator_id INT NOT NULL,
  category VARCHAR(100),
  replies_count INT DEFAULT 0,
  views INT DEFAULT 0,
  pinned BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_category (category),
  INDEX idx_pinned (pinned),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 讨论区回复
CREATE TABLE discussion_replies (
  reply_id INT PRIMARY KEY AUTO_INCREMENT,
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  content TEXT NOT NULL,
  likes INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (topic_id) REFERENCES discussion_topics(topic_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_topic (topic_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 消息通知
CREATE TABLE user_notifications (
  notification_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  notification_type VARCHAR(100),
  content TEXT,
  related_user_id INT,
  related_content_type VARCHAR(50),
  related_content_id INT,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (related_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
  INDEX idx_user_read (user_id, is_read),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 私信
CREATE TABLE private_messages (
  message_id INT PRIMARY KEY AUTO_INCREMENT,
  sender_id INT NOT NULL,
  receiver_id INT NOT NULL,
  content TEXT NOT NULL,
  is_read BOOLEAN DEFAULT FALSE,
  sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  read_at TIMESTAMP,
  FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_receiver_read (receiver_id, is_read),
  INDEX idx_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 周边商品
CREATE TABLE merchandise (
  merchandise_id INT PRIMARY KEY AUTO_INCREMENT,
  creator_id INT NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  description TEXT,
  category VARCHAR(100),
  price DECIMAL(12,2),
  stock_quantity INT DEFAULT 0,
  product_images VARCHAR(1000),
  status ENUM('draft', 'pending', 'listed', 'sold_out', 'delisted') DEFAULT 'draft',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_creator (creator_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 库存管理
CREATE TABLE merchandise_inventory (
  inventory_id INT PRIMARY KEY AUTO_INCREMENT,
  merchandise_id INT NOT NULL UNIQUE,
  quantity_available INT DEFAULT 0,
  quantity_reserved INT DEFAULT 0,
  last_stock_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (merchandise_id) REFERENCES merchandise(merchandise_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 周边销售记录
CREATE TABLE merchandise_sales (
  sale_id INT PRIMARY KEY AUTO_INCREMENT,
  merchandise_id INT NOT NULL,
  buyer_id INT NOT NULL,
  quantity INT DEFAULT 1,
  price_paid DECIMAL(12,2),
  shipping_address TEXT,
  shipping_status ENUM('pending', 'shipped', 'delivered', 'returned') DEFAULT 'pending',
  tracking_number VARCHAR(100),
  sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (merchandise_id) REFERENCES merchandise(merchandise_id) ON DELETE CASCADE,
  FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_buyer (buyer_id),
  INDEX idx_merchandise (merchandise_id),
  INDEX idx_sale_date (sale_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 虚拟主播形象
CREATE TABLE vtuber_avatars (
  avatar_id INT PRIMARY KEY AUTO_INCREMENT,
  vtuber_id INT NOT NULL,
  avatar_name VARCHAR(100),
  model_type ENUM('live2d', '3d', 'vroid', 'custom') NOT NULL,
  model_file_url VARCHAR(500),
  thumbnail_url VARCHAR(500),
  is_primary BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (vtuber_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_vtuber (vtuber_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- VTuber主页信息
CREATE TABLE vtuber_profile (
  profile_id INT PRIMARY KEY AUTO_INCREMENT,
  vtuber_id INT NOT NULL UNIQUE,
  channel_name VARCHAR(200),
  bio TEXT,
  avatar_id INT,
  follower_count INT DEFAULT 0,
  total_live_minutes INT DEFAULT 0,
  total_revenue DECIMAL(12,2) DEFAULT 0,
  subscription_status ENUM('free', 'premium') DEFAULT 'free',
  verified BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (vtuber_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (avatar_id) REFERENCES vtuber_avatars(avatar_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创作者认证
CREATE TABLE creator_verification (
  verification_id INT PRIMARY KEY AUTO_INCREMENT,
  creator_id INT NOT NULL UNIQUE,
  verification_type VARCHAR(100),
  submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  approval_date TIMESTAMP,
  status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
  FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户行为日志（审计）
CREATE TABLE user_activity_log (
  log_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  action_type VARCHAR(100),
  action_detail TEXT,
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_user_date (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 内容审核记录
CREATE TABLE content_moderation (
  moderation_id INT PRIMARY KEY AUTO_INCREMENT,
  content_type VARCHAR(100),
  content_id INT,
  content_owner_id INT,
  reported_by_id INT,
  reason TEXT,
  status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
  moderator_id INT,
  decision_reason TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  decided_at TIMESTAMP,
  FOREIGN KEY (content_owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (reported_by_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (moderator_id) REFERENCES users(user_id) ON DELETE SET NULL,
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始化资产类型
INSERT INTO asset_types (type_name, description, commission_rate) VALUES
('Live2D模型', '2D 虚拟形象模型', 20),
('3D模型', '3D 虚拟形象模型', 20),
('配音包', '语音配音和音效', 25),
('特效', '直播特效和动画', 20),
('服饰', '虚拟形象衣服和配饰', 20);

-- 授予开发用户权限
GRANT ALL PRIVILEGES ON virtuallive_dev.* TO 'virtual_live'@'%';
FLUSH PRIVILEGES;
