-- ========================================
-- 数据库增量更新脚本
-- 适用于从 init.sql 初始化的数据库
-- 创建日期: 2025-12-04
-- ========================================

USE virtuallive_dev;

-- ========================================
-- 1. 添加缺失的 fan_circles 表（粉丝圈子/社群）
-- ========================================
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

-- ========================================
-- 2. 检查并添加 vtuber_avatars 表的扩展字段
-- ========================================

-- 添加 template_id 字段（如果不存在）
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = 'virtuallive_dev' 
  AND TABLE_NAME = 'vtuber_avatars' 
  AND COLUMN_NAME = 'template_id'
);

SET @sql = IF(
  @column_exists = 0,
  'ALTER TABLE vtuber_avatars ADD COLUMN template_id INT AFTER model_type',
  'SELECT "template_id column already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 customization_data 字段（如果不存在）
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = 'virtuallive_dev' 
  AND TABLE_NAME = 'vtuber_avatars' 
  AND COLUMN_NAME = 'customization_data'
);

SET @sql = IF(
  @column_exists = 0,
  'ALTER TABLE vtuber_avatars ADD COLUMN customization_data LONGTEXT COMMENT "JSON格式的定制数据" AFTER template_id',
  'SELECT "customization_data column already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加外键约束（如果不存在）
SET @constraint_exists = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
  WHERE TABLE_SCHEMA = 'virtuallive_dev'
  AND TABLE_NAME = 'vtuber_avatars'
  AND CONSTRAINT_NAME LIKE '%template_id%'
  AND REFERENCED_TABLE_NAME = 'avatar_templates'
);

SET @sql = IF(
  @constraint_exists = 0 AND @column_exists > 0,
  'ALTER TABLE vtuber_avatars ADD FOREIGN KEY (template_id) REFERENCES avatar_templates(template_id) ON DELETE SET NULL',
  'SELECT "Foreign key for template_id already exists or column not found" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引（如果不存在）
SET @index_exists = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = 'virtuallive_dev'
  AND TABLE_NAME = 'vtuber_avatars'
  AND INDEX_NAME = 'idx_template'
);

SET @sql = IF(
  @index_exists = 0,
  'ALTER TABLE vtuber_avatars ADD INDEX idx_template (template_id)',
  'SELECT "idx_template index already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================
-- 更新完成
-- ========================================
SELECT 'Database migration completed successfully!' AS status;
