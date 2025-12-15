mysqldump: [Warning] World-writable config file '/etc/mysql/conf.d/my.cnf' is ignored.
mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: virtuallive_dev
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `asset_purchases`
--

DROP TABLE IF EXISTS `asset_purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_purchases` (
  `purchase_id` int NOT NULL AUTO_INCREMENT,
  `asset_id` int NOT NULL,
  `buyer_id` int NOT NULL,
  `price_paid` decimal(12,2) DEFAULT NULL,
  `purchase_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `download_link` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_refunded` tinyint(1) DEFAULT '0',
  `refund_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`purchase_id`),
  UNIQUE KEY `transaction_id` (`transaction_id`),
  KEY `idx_buyer` (`buyer_id`),
  KEY `idx_asset` (`asset_id`),
  KEY `idx_purchase_date` (`purchase_date`),
  CONSTRAINT `asset_purchases_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `virtual_assets` (`asset_id`) ON DELETE CASCADE,
  CONSTRAINT `asset_purchases_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_purchases`
--

LOCK TABLES `asset_purchases` WRITE;
/*!40000 ALTER TABLE `asset_purchases` DISABLE KEYS */;
/*!40000 ALTER TABLE `asset_purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asset_reviews`
--

DROP TABLE IF EXISTS `asset_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `asset_id` int NOT NULL,
  `reviewer_id` int NOT NULL,
  `rating` int DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `helpful_count` int DEFAULT '0',
  PRIMARY KEY (`review_id`),
  KEY `idx_asset` (`asset_id`),
  KEY `idx_reviewer` (`reviewer_id`),
  CONSTRAINT `asset_reviews_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `virtual_assets` (`asset_id`) ON DELETE CASCADE,
  CONSTRAINT `asset_reviews_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `asset_reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_reviews`
--

LOCK TABLES `asset_reviews` WRITE;
/*!40000 ALTER TABLE `asset_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `asset_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asset_types`
--

DROP TABLE IF EXISTS `asset_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_types` (
  `type_id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `commission_rate` decimal(5,2) DEFAULT '20.00',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name` (`type_name`),
  KEY `idx_type_name` (`type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_types`
--

LOCK TABLES `asset_types` WRITE;
/*!40000 ALTER TABLE `asset_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `asset_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asset_versions`
--

DROP TABLE IF EXISTS `asset_versions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_versions` (
  `version_id` int NOT NULL AUTO_INCREMENT,
  `asset_id` int NOT NULL,
  `version_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_size_mb` int DEFAULT NULL,
  `update_notes` text COLLATE utf8mb4_unicode_ci,
  `release_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_latest` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`version_id`),
  KEY `idx_asset` (`asset_id`),
  CONSTRAINT `asset_versions_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `virtual_assets` (`asset_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_versions`
--

LOCK TABLES `asset_versions` WRITE;
/*!40000 ALTER TABLE `asset_versions` DISABLE KEYS */;
/*!40000 ALTER TABLE `asset_versions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `avatar_templates`
--

DROP TABLE IF EXISTS `avatar_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avatar_templates` (
  `template_id` int NOT NULL AUTO_INCREMENT,
  `template_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `template_category` enum('cute_chibi','outline_avatar','expression_system') COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `template_type` enum('free','basic','premium') COLLATE utf8mb4_unicode_ci DEFAULT 'free',
  `preview_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model_data_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `is_active` tinyint(1) DEFAULT '1',
  `download_count` int DEFAULT '0',
  `usage_count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`template_id`),
  KEY `idx_category` (`template_category`),
  KEY `idx_type` (`template_type`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_name` (`template_name`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avatar_templates`
--

LOCK TABLES `avatar_templates` WRITE;
/*!40000 ALTER TABLE `avatar_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `avatar_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `circle_members`
--

DROP TABLE IF EXISTS `circle_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `circle_members` (
  `member_id` int NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) DEFAULT NULL,
  `joined_at` datetime(6) NOT NULL,
  `post_count` int DEFAULT NULL,
  `circle_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `unique_circle_member` (`circle_id`,`user_id`),
  KEY `idx_circle` (`circle_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_joined_at` (`joined_at`),
  CONSTRAINT `FKebqlug5f78ojop2j7qnbg4y15` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKk4ytdqsqxjyn3qotgshjh1ial` FOREIGN KEY (`circle_id`) REFERENCES `fan_circles` (`circle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `circle_members`
--

LOCK TABLES `circle_members` WRITE;
/*!40000 ALTER TABLE `circle_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `circle_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  `parent_comment_id` int DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `likes` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `parent_comment_id` (`parent_comment_id`),
  KEY `idx_post` (`post_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `community_posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_posts`
--

DROP TABLE IF EXISTS `community_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_posts` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `author_id` int NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tags` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cover_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `likes` int DEFAULT '0',
  `comments_count` int DEFAULT '0',
  `views` int DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `duration` int DEFAULT NULL,
  `video_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `idx_author` (`author_id`),
  KEY `idx_category` (`category`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `community_posts_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_posts`
--

LOCK TABLES `community_posts` WRITE;
/*!40000 ALTER TABLE `community_posts` DISABLE KEYS */;
/*!40000 ALTER TABLE `community_posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_moderation`
--

DROP TABLE IF EXISTS `content_moderation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `content_moderation` (
  `moderation_id` int NOT NULL AUTO_INCREMENT,
  `content_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content_id` int DEFAULT NULL,
  `content_owner_id` int DEFAULT NULL,
  `reported_by_id` int DEFAULT NULL,
  `reason` text COLLATE utf8mb4_unicode_ci,
  `status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `moderator_id` int DEFAULT NULL,
  `decision_reason` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `decided_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`moderation_id`),
  KEY `content_owner_id` (`content_owner_id`),
  KEY `reported_by_id` (`reported_by_id`),
  KEY `moderator_id` (`moderator_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `content_moderation_ibfk_1` FOREIGN KEY (`content_owner_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `content_moderation_ibfk_2` FOREIGN KEY (`reported_by_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `content_moderation_ibfk_3` FOREIGN KEY (`moderator_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_moderation`
--

LOCK TABLES `content_moderation` WRITE;
/*!40000 ALTER TABLE `content_moderation` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_moderation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creator_revenue`
--

DROP TABLE IF EXISTS `creator_revenue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creator_revenue` (
  `revenue_id` int NOT NULL AUTO_INCREMENT,
  `creator_id` int NOT NULL,
  `asset_id` int DEFAULT NULL,
  `revenue_type` enum('asset_sale','commission','tip') COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `commission_rate` decimal(5,2) DEFAULT NULL,
  `revenue_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `settlement_status` enum('pending','settled','failed') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  PRIMARY KEY (`revenue_id`),
  KEY `asset_id` (`asset_id`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_revenue_date` (`revenue_date`),
  CONSTRAINT `creator_revenue_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `creator_revenue_ibfk_2` FOREIGN KEY (`asset_id`) REFERENCES `virtual_assets` (`asset_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creator_revenue`
--

LOCK TABLES `creator_revenue` WRITE;
/*!40000 ALTER TABLE `creator_revenue` DISABLE KEYS */;
/*!40000 ALTER TABLE `creator_revenue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creator_verification`
--

DROP TABLE IF EXISTS `creator_verification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creator_verification` (
  `verification_id` int NOT NULL AUTO_INCREMENT,
  `creator_id` int NOT NULL,
  `verification_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `submission_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `approval_date` timestamp NULL DEFAULT NULL,
  `status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  PRIMARY KEY (`verification_id`),
  UNIQUE KEY `creator_id` (`creator_id`),
  CONSTRAINT `creator_verification_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creator_verification`
--

LOCK TABLES `creator_verification` WRITE;
/*!40000 ALTER TABLE `creator_verification` DISABLE KEYS */;
/*!40000 ALTER TABLE `creator_verification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cute_chibi_templates`
--

DROP TABLE IF EXISTS `cute_chibi_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cute_chibi_templates` (
  `chibi_id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NOT NULL,
  `body_style` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `head_shape` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `eye_style` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mouth_style` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hair_template` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `color_preset_count` int DEFAULT '0',
  `animation_enabled` tinyint(1) DEFAULT '1',
  `customizable_parts` longtext COLLATE utf8mb4_unicode_ci COMMENT 'JSONæ ¼å¼ï¼šå¯å®šåˆ¶éƒ¨ä»¶åˆ—è¡¨',
  PRIMARY KEY (`chibi_id`),
  UNIQUE KEY `template_id` (`template_id`),
  KEY `idx_template` (`template_id`),
  CONSTRAINT `cute_chibi_templates_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `avatar_templates` (`template_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cute_chibi_templates`
--

LOCK TABLES `cute_chibi_templates` WRITE;
/*!40000 ALTER TABLE `cute_chibi_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `cute_chibi_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_checkins`
--

DROP TABLE IF EXISTS `daily_checkins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_checkins` (
  `checkin_id` int NOT NULL AUTO_INCREMENT,
  `checkin_date` date NOT NULL,
  `continuous_days` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `reward_points` int DEFAULT NULL,
  `total_days` int DEFAULT NULL,
  `circle_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`checkin_id`),
  UNIQUE KEY `unique_user_circle_date` (`user_id`,`circle_id`,`checkin_date`),
  KEY `idx_user_circle` (`user_id`,`circle_id`),
  KEY `idx_checkin_date` (`checkin_date`),
  KEY `FK99yh168qot4kn4ki2lbf0m96g` (`circle_id`),
  CONSTRAINT `FK99yh168qot4kn4ki2lbf0m96g` FOREIGN KEY (`circle_id`) REFERENCES `fan_circles` (`circle_id`),
  CONSTRAINT `FKmi5uo39gywhi6xuca1lnxy4c9` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_checkins`
--

LOCK TABLES `daily_checkins` WRITE;
/*!40000 ALTER TABLE `daily_checkins` DISABLE KEYS */;
/*!40000 ALTER TABLE `daily_checkins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `danmaku`
--

DROP TABLE IF EXISTS `danmaku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danmaku` (
  `danmaku_id` int NOT NULL AUTO_INCREMENT,
  `session_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`danmaku_id`),
  KEY `idx_session` (`session_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `danmaku_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `live_sessions` (`session_id`) ON DELETE CASCADE,
  CONSTRAINT `danmaku_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danmaku`
--

LOCK TABLES `danmaku` WRITE;
/*!40000 ALTER TABLE `danmaku` DISABLE KEYS */;
INSERT INTO `danmaku` VALUES (2,20,88,'主播好帅！','#ff0000','2025-12-08 03:42:15',0),(3,20,88,'主播好帅！','#ff0000','2025-12-08 03:43:58',0),(6,19,88,'主播好帅！','#d63838','2025-12-10 01:01:40',0),(8,26,88,'主播好帅！123','#af5555','2025-12-10 01:05:49',0),(9,26,88,'主播好帅！','#d50707','2025-12-10 01:10:45',0),(10,26,88,'[SC 200s] 加油加油！',NULL,'2025-12-10 01:10:50',0),(11,26,88,'[SC 200s] 加油加油！',NULL,'2025-12-10 01:11:20',0),(12,26,88,'主播好帅！','#a66464','2025-12-10 01:12:06',0),(13,26,88,'主播好帅！','#000000','2025-12-10 01:23:55',0),(14,26,88,'主播好帅！','#000000','2025-12-10 01:24:04',1),(15,26,88,'主播好帅！12','#000000','2025-12-10 01:24:11',0),(19,27,88,'主播好帅！','#000000','2025-12-10 02:27:25',0),(20,27,88,'测试弹幕','#000','2025-12-10 02:27:30',0),(21,27,88,'主播好帅！','#000000','2025-12-10 02:52:49',0),(22,27,88,'[SC 200s] 加油加油！',NULL,'2025-12-10 02:54:13',0),(23,27,88,'主播好帅！','#000000','2025-12-10 03:06:20',0),(24,28,88,'主播好帅！','#000000','2025-12-10 03:15:28',0),(25,28,88,'[SC 200s] 加油加油！',NULL,'2025-12-10 03:15:53',0),(26,28,88,'主播好帅！','#000000','2025-12-10 03:17:18',1),(27,28,88,'[SC 200s] 加油加油！',NULL,'2025-12-10 03:19:23',0),(28,28,88,'主播好帅！','#000000','2025-12-10 04:17:20',0);
/*!40000 ALTER TABLE `danmaku` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion_replies`
--

DROP TABLE IF EXISTS `discussion_replies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion_replies` (
  `reply_id` int NOT NULL AUTO_INCREMENT,
  `topic_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `likes` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`reply_id`),
  KEY `idx_topic` (`topic_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `discussion_replies_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `discussion_topics` (`topic_id`) ON DELETE CASCADE,
  CONSTRAINT `discussion_replies_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion_replies`
--

LOCK TABLES `discussion_replies` WRITE;
/*!40000 ALTER TABLE `discussion_replies` DISABLE KEYS */;
/*!40000 ALTER TABLE `discussion_replies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion_topics`
--

DROP TABLE IF EXISTS `discussion_topics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion_topics` (
  `topic_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `creator_id` int NOT NULL,
  `category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `replies_count` int DEFAULT '0',
  `views` int DEFAULT '0',
  `pinned` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`topic_id`),
  KEY `creator_id` (`creator_id`),
  KEY `idx_category` (`category`),
  KEY `idx_pinned` (`pinned`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `discussion_topics_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion_topics`
--

LOCK TABLES `discussion_topics` WRITE;
/*!40000 ALTER TABLE `discussion_topics` DISABLE KEYS */;
/*!40000 ALTER TABLE `discussion_topics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expression_library`
--

DROP TABLE IF EXISTS `expression_library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expression_library` (
  `expression_lib_id` int NOT NULL AUTO_INCREMENT,
  `expression_template_id` int NOT NULL,
  `expression_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expression_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'è¡¨æƒ…ä»£ç ï¼šblink/nod/clapç­‰',
  `expression_type` enum('eye','face','body','hand','full_body') COLLATE utf8mb4_unicode_ci NOT NULL,
  `animation_file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `animation_duration_ms` int DEFAULT NULL COMMENT 'åŠ¨ç”»æ—¶é•¿ï¼ˆæ¯«ç§’ï¼‰',
  `preview_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_loopable` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`expression_lib_id`),
  UNIQUE KEY `expression_code` (`expression_code`),
  KEY `idx_template` (`expression_template_id`),
  KEY `idx_code` (`expression_code`),
  CONSTRAINT `expression_library_ibfk_1` FOREIGN KEY (`expression_template_id`) REFERENCES `expression_system_templates` (`expression_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expression_library`
--

LOCK TABLES `expression_library` WRITE;
/*!40000 ALTER TABLE `expression_library` DISABLE KEYS */;
/*!40000 ALTER TABLE `expression_library` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expression_system_templates`
--

DROP TABLE IF EXISTS `expression_system_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expression_system_templates` (
  `expression_id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NOT NULL,
  `base_model_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `included_expressions` longtext COLLATE utf8mb4_unicode_ci COMMENT 'JSONæ ¼å¼ï¼šåŒ…å«çš„è¡¨æƒ…åˆ—è¡¨',
  `expression_count` int DEFAULT '0',
  `supports_custom_expressions` tinyint(1) DEFAULT '0',
  `animation_format` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'åŠ¨ç”»æ ¼å¼ï¼šGIF/WebP/MP4ç­‰',
  PRIMARY KEY (`expression_id`),
  UNIQUE KEY `template_id` (`template_id`),
  KEY `idx_template` (`template_id`),
  CONSTRAINT `expression_system_templates_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `avatar_templates` (`template_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expression_system_templates`
--

LOCK TABLES `expression_system_templates` WRITE;
/*!40000 ALTER TABLE `expression_system_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `expression_system_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fan_badges`
--

DROP TABLE IF EXISTS `fan_badges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fan_badges` (
  `badge_id` int NOT NULL AUTO_INCREMENT,
  `vtuber_id` int NOT NULL,
  `fan_id` int NOT NULL,
  `badge_level` int DEFAULT '1',
  `badge_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `acquired_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`badge_id`),
  KEY `idx_vtuber` (`vtuber_id`),
  KEY `idx_fan` (`fan_id`),
  CONSTRAINT `fan_badges_ibfk_1` FOREIGN KEY (`vtuber_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fan_badges_ibfk_2` FOREIGN KEY (`fan_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fan_badges`
--

LOCK TABLES `fan_badges` WRITE;
/*!40000 ALTER TABLE `fan_badges` DISABLE KEYS */;
/*!40000 ALTER TABLE `fan_badges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fan_circles`
--

DROP TABLE IF EXISTS `fan_circles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fan_circles` (
  `circle_id` int NOT NULL AUTO_INCREMENT,
  `avatar_url` varchar(500) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `cover_image_url` varchar(500) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_official` bit(1) DEFAULT NULL,
  `member_count` int DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `post_count` int DEFAULT NULL,
  PRIMARY KEY (`circle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fan_circles`
--

LOCK TABLES `fan_circles` WRITE;
/*!40000 ALTER TABLE `fan_circles` DISABLE KEYS */;
/*!40000 ALTER TABLE `fan_circles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `favorite_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `content_type` enum('asset','post') COLLATE utf8mb4_unicode_ci NOT NULL,
  `content_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `unique_favorite` (`user_id`,`content_type`,`content_id`),
  KEY `idx_user_type` (`user_id`,`content_type`),
  KEY `idx_user_content` (`user_id`,`content_type`,`content_id`),
  KEY `idx_user_created` (`user_id`,`created_at`),
  CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'virtual_live','2025-11-26 07:58:38',0,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gift_donations`
--

DROP TABLE IF EXISTS `gift_donations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gift_donations` (
  `donation_id` int NOT NULL AUTO_INCREMENT,
  `session_id` int NOT NULL,
  `sender_id` int NOT NULL,
  `gift_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gift_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gift_value` decimal(12,2) NOT NULL,
  `quantity` int DEFAULT '1',
  `total_value` decimal(12,2) NOT NULL,
  `donation_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`donation_id`),
  KEY `idx_session` (`session_id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_donation_time` (`donation_time`),
  CONSTRAINT `gift_donations_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `live_sessions` (`session_id`) ON DELETE CASCADE,
  CONSTRAINT `gift_donations_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gift_donations`
--

LOCK TABLES `gift_donations` WRITE;
/*!40000 ALTER TABLE `gift_donations` DISABLE KEYS */;
INSERT INTO `gift_donations` VALUES (1,20,88,'GIFT','火箭',100.00,1,100.00,'2025-12-08 03:43:59'),(2,20,88,'GIFT','火箭',99.00,1,99.00,'2025-12-08 03:44:43'),(4,26,88,'SC',NULL,50.00,1,50.00,'2025-12-10 01:10:49'),(5,26,88,'GIFT','火箭',100.00,1,100.00,'2025-12-10 01:10:55'),(6,26,88,'SC',NULL,50.00,1,50.00,'2025-12-10 01:11:20'),(7,27,88,'SC',NULL,50.00,1,50.00,'2025-12-10 02:54:13'),(8,27,88,'GIFT','火箭',100.00,1,100.00,'2025-12-10 02:54:16'),(9,28,88,'SC',NULL,50.00,1,50.00,'2025-12-10 03:15:53'),(10,28,88,'GIFT','火箭',100.00,1,100.00,'2025-12-10 03:16:00'),(11,28,88,'SC',NULL,50.00,1,50.00,'2025-12-10 03:19:23'),(12,28,88,'GIFT','火箭',100.00,1,100.00,'2025-12-10 03:19:25');
/*!40000 ALTER TABLE `gift_donations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `like_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `content_type` enum('asset','comment','post') COLLATE utf8mb4_unicode_ci NOT NULL,
  `content_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `unique_like` (`user_id`,`content_type`,`content_id`),
  KEY `idx_content` (`content_type`,`content_id`),
  KEY `idx_user_content` (`user_id`,`content_type`,`content_id`),
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `live_playbacks`
--

DROP TABLE IF EXISTS `live_playbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `live_playbacks` (
  `playback_id` int NOT NULL AUTO_INCREMENT,
  `session_id` int NOT NULL,
  `video_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration_minutes` int DEFAULT NULL,
  `size_mb` int DEFAULT NULL,
  `status` enum('processing','available','deleted') COLLATE utf8mb4_unicode_ci DEFAULT 'processing',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`playback_id`),
  KEY `idx_session` (`session_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `live_playbacks_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `live_sessions` (`session_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `live_playbacks`
--

LOCK TABLES `live_playbacks` WRITE;
/*!40000 ALTER TABLE `live_playbacks` DISABLE KEYS */;
/*!40000 ALTER TABLE `live_playbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `live_rooms`
--

DROP TABLE IF EXISTS `live_rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `live_rooms` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `vtuber_id` int NOT NULL,
  `room_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_live` tinyint(1) DEFAULT '0',
  `stream_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rtmp_server` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `stream_key` (`stream_key`),
  KEY `idx_vtuber` (`vtuber_id`),
  KEY `idx_is_live` (`is_live`),
  CONSTRAINT `live_rooms_ibfk_1` FOREIGN KEY (`vtuber_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `live_rooms`
--

LOCK TABLES `live_rooms` WRITE;
/*!40000 ALTER TABLE `live_rooms` DISABLE KEYS */;
INSERT INTO `live_rooms` VALUES (1,1,'测试直播间',NULL,NULL,NULL,0,'my_secret_key_2025',NULL,'2025-11-30 16:05:22'),(21,88,'我的精彩直播-23:13:55',NULL,'Life','https://api.dicebear.com/7.x/shapes/svg?seed=Room',0,'u88_457ba718',NULL,'2025-12-01 07:13:56'),(101,11,'今晚吃鸡大吉大利','新人求关注','Game','http://example.com/cover.jpg',1,'room_101_04151186','rtmp://localhost/live','2025-12-09 14:59:40');
/*!40000 ALTER TABLE `live_rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `live_sessions`
--

DROP TABLE IF EXISTS `live_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `live_sessions` (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `start_time` timestamp NOT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `duration_minutes` int DEFAULT NULL,
  `viewer_count` int DEFAULT '0',
  `peak_viewers` int DEFAULT '0',
  `total_gifts_revenue` decimal(12,2) DEFAULT '0.00',
  PRIMARY KEY (`session_id`),
  KEY `idx_room` (`room_id`),
  KEY `idx_start_time` (`start_time`),
  CONSTRAINT `live_sessions_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `live_rooms` (`room_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `live_sessions`
--

LOCK TABLES `live_sessions` WRITE;
/*!40000 ALTER TABLE `live_sessions` DISABLE KEYS */;
INSERT INTO `live_sessions` VALUES (1,1,'2025-11-30 08:08:47','2025-11-30 08:12:52',NULL,0,0,0.00),(2,1,'2025-11-30 08:13:44','2025-11-30 08:20:53',NULL,0,0,0.00),(3,1,'2025-11-30 18:02:13','2025-11-30 18:06:10',NULL,0,0,0.00),(4,1,'2025-11-30 18:19:21','2025-11-30 18:24:41',NULL,0,0,0.00),(5,1,'2025-11-30 19:12:58','2025-11-30 19:44:26',NULL,0,0,0.00),(6,1,'2025-11-30 19:44:33','2025-11-30 19:48:32',NULL,0,0,0.00),(7,1,'2025-12-01 06:15:54','2025-12-01 08:00:43',NULL,0,0,0.00),(8,1,'2025-12-02 18:13:07','2025-12-02 18:15:51',NULL,0,0,0.00),(9,1,'2025-12-02 18:16:44','2025-12-02 18:26:48',NULL,0,0,0.00),(10,1,'2025-12-06 03:51:05','2025-12-06 03:51:23',NULL,0,0,0.00),(11,1,'2025-12-06 12:07:17','2025-12-06 12:07:20',NULL,0,0,0.00),(12,1,'2025-12-06 12:19:35','2025-12-06 12:19:39',NULL,0,0,0.00),(13,1,'2025-12-07 02:33:43','2025-12-07 02:52:56',NULL,0,0,0.00),(14,1,'2025-12-07 03:42:53','2025-12-07 03:42:56',NULL,0,0,0.00),(15,1,'2025-12-08 02:26:09','2025-12-08 02:27:26',NULL,0,NULL,0.00),(16,1,'2025-12-08 03:13:12',NULL,NULL,0,NULL,0.00),(17,1,'2025-12-08 03:22:31',NULL,NULL,0,NULL,0.00),(18,1,'2025-12-08 03:31:25',NULL,NULL,0,NULL,0.00),(19,1,'2025-12-08 03:34:37',NULL,NULL,0,NULL,0.00),(20,1,'2025-12-08 03:38:30','2025-12-08 04:04:14',NULL,0,NULL,199.00),(21,1,'2025-12-09 15:25:55','2025-12-09 15:26:13',NULL,0,NULL,0.00),(22,1,'2025-12-09 15:26:34','2025-12-09 15:35:19',NULL,0,NULL,0.00),(23,1,'2025-12-10 00:12:52','2025-12-10 00:20:47',NULL,0,NULL,0.00),(24,101,'2025-12-10 00:21:33','2025-12-10 00:21:35',NULL,0,NULL,0.00),(25,101,'2025-12-10 00:22:28','2025-12-10 00:22:47',NULL,0,NULL,0.00),(26,101,'2025-12-10 01:04:35','2025-12-10 02:02:17',NULL,0,NULL,200.00),(27,101,'2025-12-10 02:27:23',NULL,NULL,0,NULL,150.00),(28,101,'2025-12-10 03:15:10',NULL,NULL,0,NULL,300.00);
/*!40000 ALTER TABLE `live_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchandise`
--

DROP TABLE IF EXISTS `merchandise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchandise` (
  `merchandise_id` int NOT NULL AUTO_INCREMENT,
  `creator_id` int NOT NULL,
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(12,2) DEFAULT NULL,
  `stock_quantity` int DEFAULT '0',
  `product_images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('draft','pending','listed','sold_out','delisted') COLLATE utf8mb4_unicode_ci DEFAULT 'draft',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`merchandise_id`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `merchandise_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchandise`
--

LOCK TABLES `merchandise` WRITE;
/*!40000 ALTER TABLE `merchandise` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchandise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchandise_inventory`
--

DROP TABLE IF EXISTS `merchandise_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchandise_inventory` (
  `inventory_id` int NOT NULL AUTO_INCREMENT,
  `merchandise_id` int NOT NULL,
  `quantity_available` int DEFAULT '0',
  `quantity_reserved` int DEFAULT '0',
  `last_stock_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `merchandise_id` (`merchandise_id`),
  CONSTRAINT `merchandise_inventory_ibfk_1` FOREIGN KEY (`merchandise_id`) REFERENCES `merchandise` (`merchandise_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchandise_inventory`
--

LOCK TABLES `merchandise_inventory` WRITE;
/*!40000 ALTER TABLE `merchandise_inventory` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchandise_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchandise_sales`
--

DROP TABLE IF EXISTS `merchandise_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchandise_sales` (
  `sale_id` int NOT NULL AUTO_INCREMENT,
  `merchandise_id` int NOT NULL,
  `buyer_id` int NOT NULL,
  `quantity` int DEFAULT '1',
  `price_paid` decimal(12,2) DEFAULT NULL,
  `shipping_address` text COLLATE utf8mb4_unicode_ci,
  `shipping_status` enum('pending','shipped','delivered','returned') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `tracking_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sale_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sale_id`),
  KEY `idx_buyer` (`buyer_id`),
  KEY `idx_merchandise` (`merchandise_id`),
  KEY `idx_sale_date` (`sale_date`),
  CONSTRAINT `merchandise_sales_ibfk_1` FOREIGN KEY (`merchandise_id`) REFERENCES `merchandise` (`merchandise_id`) ON DELETE CASCADE,
  CONSTRAINT `merchandise_sales_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchandise_sales`
--

LOCK TABLES `merchandise_sales` WRITE;
/*!40000 ALTER TABLE `merchandise_sales` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchandise_sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outline_avatar_templates`
--

DROP TABLE IF EXISTS `outline_avatar_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outline_avatar_templates` (
  `outline_id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NOT NULL,
  `base_shape` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `body_proportions` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `facial_features` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customizable_elements` longtext COLLATE utf8mb4_unicode_ci COMMENT 'JSONæ ¼å¼ï¼šå¯å®šåˆ¶å…ƒç´ åˆ—è¡¨',
  `default_colors` longtext COLLATE utf8mb4_unicode_ci COMMENT 'JSONæ ¼å¼ï¼šé»˜è®¤è‰²å½©æ–¹æ¡ˆ',
  `supports_animation` tinyint(1) DEFAULT '0',
  `max_customization_level` int DEFAULT '3',
  PRIMARY KEY (`outline_id`),
  UNIQUE KEY `template_id` (`template_id`),
  KEY `idx_template` (`template_id`),
  CONSTRAINT `outline_avatar_templates_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `avatar_templates` (`template_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outline_avatar_templates`
--

LOCK TABLES `outline_avatar_templates` WRITE;
/*!40000 ALTER TABLE `outline_avatar_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `outline_avatar_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `private_messages`
--

DROP TABLE IF EXISTS `private_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `private_messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `sender_id` (`sender_id`),
  KEY `idx_receiver_read` (`receiver_id`,`is_read`),
  KEY `idx_sent_at` (`sent_at`),
  CONSTRAINT `private_messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `private_messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `private_messages`
--

LOCK TABLES `private_messages` WRITE;
/*!40000 ALTER TABLE `private_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `private_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_bans`
--

DROP TABLE IF EXISTS `room_bans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_bans` (
  `ban_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `room_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`ban_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_bans`
--

LOCK TABLES `room_bans` WRITE;
/*!40000 ALTER TABLE `room_bans` DISABLE KEYS */;
INSERT INTO `room_bans` VALUES (1,'2025-12-10 09:12:14.513058','2025-12-10 09:13:14.507787','主播/房管禁言',101,88),(2,'2025-12-10 09:24:15.498397','2025-12-10 09:25:15.493451','主播/房管禁言',101,88),(3,'2025-12-10 09:43:01.372413','2025-12-10 09:44:01.367578','主播/房管禁言',101,88),(4,'2025-12-10 09:45:48.659553','2025-12-10 09:46:48.655020','主播/房管禁言',101,88),(5,'2025-12-10 10:27:51.900683','2025-12-10 10:28:51.895679','主播/房管禁言',101,88),(6,'2025-12-10 10:28:07.825650','2025-12-10 10:29:07.821944','主播/房管禁言',101,88),(7,'2025-12-10 10:28:15.068967','2025-12-10 10:29:15.064211','主播/房管禁言',101,88),(8,'2025-12-10 10:28:15.371545','2025-12-10 10:29:15.368581','主播/房管禁言',101,88),(9,'2025-12-10 10:28:15.583706','2025-12-10 10:29:15.581303','主播/房管禁言',101,88),(10,'2025-12-10 10:28:15.756052','2025-12-10 10:29:15.753006','主播/房管禁言',101,88),(11,'2025-12-10 10:28:24.181846','2025-12-10 10:29:24.177640','主播/房管禁言',101,88),(12,'2025-12-10 10:52:52.231279','2025-12-10 10:53:52.226768','主播/房管禁言',101,88),(13,'2025-12-10 11:06:21.702449','2025-12-10 11:07:21.698759','主播/房管禁言',101,88),(14,'2025-12-10 11:08:53.470173','2025-12-10 11:09:53.467230','主播/房管禁言',101,88),(15,'2025-12-10 11:15:25.334834','2025-12-10 11:15:25.192840','主播/房管禁言',101,88),(16,'2025-12-10 11:19:18.543488','2025-12-10 11:20:18.539341','主播/房管禁言',101,88);
/*!40000 ALTER TABLE `room_bans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_activity_log`
--

DROP TABLE IF EXISTS `user_activity_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_activity_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `action_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action_detail` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`),
  KEY `idx_user_date` (`user_id`,`created_at`),
  CONSTRAINT `user_activity_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activity_log`
--

LOCK TABLES `user_activity_log` WRITE;
/*!40000 ALTER TABLE `user_activity_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_activity_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_custom_avatars`
--

DROP TABLE IF EXISTS `user_custom_avatars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_custom_avatars` (
  `custom_avatar_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `template_id` int NOT NULL,
  `avatar_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customization_data` longtext COLLATE utf8mb4_unicode_ci COMMENT 'JSONæ ¼å¼ï¼šç”¨æˆ·çš„å®šåˆ¶å‚æ•°',
  `preview_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_primary` tinyint(1) DEFAULT '0',
  `usage_count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`custom_avatar_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_template` (`template_id`),
  KEY `idx_is_primary` (`is_primary`),
  CONSTRAINT `user_custom_avatars_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_custom_avatars_ibfk_2` FOREIGN KEY (`template_id`) REFERENCES `avatar_templates` (`template_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_custom_avatars`
--

LOCK TABLES `user_custom_avatars` WRITE;
/*!40000 ALTER TABLE `user_custom_avatars` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_custom_avatars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_expression_usage`
--

DROP TABLE IF EXISTS `user_expression_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_expression_usage` (
  `usage_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `expression_lib_id` int NOT NULL,
  `used_count` int DEFAULT '1',
  `last_used_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`usage_id`),
  UNIQUE KEY `unique_user_expression` (`user_id`,`expression_lib_id`),
  KEY `expression_lib_id` (`expression_lib_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_last_used` (`last_used_at`),
  CONSTRAINT `user_expression_usage_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_expression_usage_ibfk_2` FOREIGN KEY (`expression_lib_id`) REFERENCES `expression_library` (`expression_lib_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_expression_usage`
--

LOCK TABLES `user_expression_usage` WRITE;
/*!40000 ALTER TABLE `user_expression_usage` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_expression_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_follows`
--

DROP TABLE IF EXISTS `user_follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_follows` (
  `follow_id` int NOT NULL AUTO_INCREMENT,
  `follower_id` int NOT NULL,
  `following_id` int NOT NULL,
  `follow_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_blocked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`follow_id`),
  UNIQUE KEY `unique_follow` (`follower_id`,`following_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_following` (`following_id`),
  CONSTRAINT `user_follows_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_follows_ibfk_2` FOREIGN KEY (`following_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_follows`
--

LOCK TABLES `user_follows` WRITE;
/*!40000 ALTER TABLE `user_follows` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_notifications`
--

DROP TABLE IF EXISTS `user_notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `notification_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `related_user_id` int DEFAULT NULL,
  `related_content_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `related_content_id` int DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `related_user_id` (`related_user_id`),
  KEY `idx_user_read` (`user_id`,`is_read`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `user_notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_notifications_ibfk_2` FOREIGN KEY (`related_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_notifications`
--

LOCK TABLES `user_notifications` WRITE;
/*!40000 ALTER TABLE `user_notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_wallet`
--

DROP TABLE IF EXISTS `user_wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_wallet` (
  `wallet_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `balance` decimal(12,2) DEFAULT '0.00',
  `total_earned` decimal(12,2) DEFAULT '0.00',
  `total_spent` decimal(12,2) DEFAULT '0.00',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`wallet_id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_wallet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_wallet`
--

LOCK TABLES `user_wallet` WRITE;
/*!40000 ALTER TABLE `user_wallet` DISABLE KEYS */;
INSERT INTO `user_wallet` VALUES (1,88,9151.00,0.00,849.00,'2025-12-10 03:19:25'),(2,22,1000.00,0.00,0.00,'2025-12-09 14:59:40'),(3,33,50.00,0.00,0.00,'2025-12-09 14:59:40');
/*!40000 ALTER TABLE `user_wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_type` enum('vtuber','creator','fan','merchant') COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `introduction` text COLLATE utf8mb4_unicode_ci,
  `registration_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `status` enum('active','banned','inactive') COLLATE utf8mb4_unicode_ci DEFAULT 'active',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin_test','admin@test.com','123',NULL,'vtuber',NULL,NULL,'2025-11-30 16:05:22',NULL,'active'),(11,'Vtuber_Alice','alice@live.com','hash123',NULL,'vtuber','http://img/alice.jpg',NULL,'2025-12-09 14:59:40',NULL,'active'),(22,'Fan_Bob','bob@gmail.com','hash456',NULL,'fan','http://img/bob.jpg',NULL,'2025-12-09 14:59:40',NULL,'active'),(33,'Fan_Charlie','charlie@gmail.com','hash789',NULL,'fan','http://img/charlie.jpg',NULL,'2025-12-09 14:59:40',NULL,'active'),(88,'TestUser','test@live.com','hashed_pwd',NULL,'vtuber',NULL,NULL,'2025-12-01 15:12:10',NULL,'active');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `view_history`
--

DROP TABLE IF EXISTS `view_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `view_history` (
  `view_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `session_id` int NOT NULL,
  `watch_start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `watch_duration_minutes` int DEFAULT NULL,
  PRIMARY KEY (`view_id`),
  KEY `session_id` (`session_id`),
  KEY `idx_user_session` (`user_id`,`session_id`),
  CONSTRAINT `view_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `view_history_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `live_sessions` (`session_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `view_history`
--

LOCK TABLES `view_history` WRITE;
/*!40000 ALTER TABLE `view_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `view_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `virtual_assets`
--

DROP TABLE IF EXISTS `virtual_assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `virtual_assets` (
  `asset_id` int NOT NULL AUTO_INCREMENT,
  `creator_id` int NOT NULL,
  `asset_type_id` int NOT NULL,
  `asset_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `preview_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tags` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(12,2) DEFAULT NULL,
  `free_download` tinyint(1) DEFAULT '0',
  `status` enum('draft','pending_review','approved','rejected','delisted') COLLATE utf8mb4_unicode_ci DEFAULT 'draft',
  `download_count` int DEFAULT '0',
  `purchase_count` int DEFAULT '0',
  `rating` decimal(3,2) DEFAULT '0.00',
  `rating_count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`asset_id`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_asset_type` (`asset_type_id`),
  CONSTRAINT `virtual_assets_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `virtual_assets_ibfk_2` FOREIGN KEY (`asset_type_id`) REFERENCES `asset_types` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `virtual_assets`
--

LOCK TABLES `virtual_assets` WRITE;
/*!40000 ALTER TABLE `virtual_assets` DISABLE KEYS */;
/*!40000 ALTER TABLE `virtual_assets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vtuber_avatars`
--

DROP TABLE IF EXISTS `vtuber_avatars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vtuber_avatars` (
  `avatar_id` int NOT NULL AUTO_INCREMENT,
  `vtuber_id` int NOT NULL,
  `avatar_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model_type` enum('live2d','3d','vroid','custom') COLLATE utf8mb4_unicode_ci NOT NULL,
  `model_file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_primary` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`avatar_id`),
  KEY `idx_vtuber` (`vtuber_id`),
  CONSTRAINT `vtuber_avatars_ibfk_1` FOREIGN KEY (`vtuber_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vtuber_avatars`
--

LOCK TABLES `vtuber_avatars` WRITE;
/*!40000 ALTER TABLE `vtuber_avatars` DISABLE KEYS */;
/*!40000 ALTER TABLE `vtuber_avatars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vtuber_profile`
--

DROP TABLE IF EXISTS `vtuber_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vtuber_profile` (
  `profile_id` int NOT NULL AUTO_INCREMENT,
  `vtuber_id` int NOT NULL,
  `channel_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bio` text COLLATE utf8mb4_unicode_ci,
  `avatar_id` int DEFAULT NULL,
  `follower_count` int DEFAULT '0',
  `total_live_minutes` int DEFAULT '0',
  `total_revenue` decimal(12,2) DEFAULT '0.00',
  `subscription_status` enum('free','premium') COLLATE utf8mb4_unicode_ci DEFAULT 'free',
  `verified` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `vtuber_id` (`vtuber_id`),
  KEY `avatar_id` (`avatar_id`),
  CONSTRAINT `vtuber_profile_ibfk_1` FOREIGN KEY (`vtuber_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `vtuber_profile_ibfk_2` FOREIGN KEY (`avatar_id`) REFERENCES `vtuber_avatars` (`avatar_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vtuber_profile`
--

LOCK TABLES `vtuber_profile` WRITE;
/*!40000 ALTER TABLE `vtuber_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `vtuber_profile` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-14 22:56:58
