package com.virtuallive.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库结构检查与自动修复工具
 * 启动时自动检查并修复关键表结构
 */
// @Component
public class DatabaseStructureChecker implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== 正在检查并修复数据库结构 ===\n");
        try (Connection conn = dataSource.getConnection()) {
            // 1. 修复 danmaku 表结构
            fixDanmakuTable(conn);
            
            // 2. 修复 community_posts 表结构
            fixCommunityPostsTable(conn);
        }
        System.out.println("\n=== 数据库结构检查完成 ===\n");
    }

    private void fixDanmakuTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            System.out.println("正在检查 danmaku 表...");
            
            // 1. 允许 session_id 为空
            try {
                stmt.execute("ALTER TABLE danmaku MODIFY COLUMN session_id INT NULL");
                System.out.println("SUCCESS: 已将 danmaku.session_id 设置为可空");
            } catch (Exception e) {
                System.out.println("INFO: 修改 session_id 失败 (可能已正确): " + e.getMessage());
            }

            // 2. 添加 video_id 列
            if (!columnExists(conn, "danmaku", "video_id")) {
                try {
                    stmt.execute("ALTER TABLE danmaku ADD COLUMN video_id INT NULL AFTER session_id");
                    stmt.execute("ALTER TABLE danmaku ADD INDEX idx_video (video_id)");
                    System.out.println("SUCCESS: 已添加 danmaku.video_id 列");
                } catch (Exception e) {
                    System.err.println("ERROR: 添加 video_id 失败: " + e.getMessage());
                }
            }

            // 3. 添加 video_time 列
            if (!columnExists(conn, "danmaku", "video_time")) {
                try {
                    stmt.execute("ALTER TABLE danmaku ADD COLUMN video_time FLOAT NULL COMMENT 'Video playback time in seconds'");
                    System.out.println("SUCCESS: 已添加 danmaku.video_time 列");
                } catch (Exception e) {
                    System.err.println("ERROR: 添加 video_time 失败: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: 修复 danmaku 表时发生错误: " + e.getMessage());
        }
    }

    private void fixCommunityPostsTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // 添加 video_url 列
            if (!columnExists(conn, "community_posts", "video_url")) {
                try {
                    stmt.execute("ALTER TABLE community_posts ADD COLUMN video_url VARCHAR(500) AFTER cover_image_url");
                    System.out.println("SUCCESS: 已添加 community_posts.video_url 列");
                } catch (Exception e) {
                    System.err.println("ERROR: 添加 video_url 失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: 修复 community_posts 表时发生错误: " + e.getMessage());
        }
    }

    private boolean columnExists(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, tableName, columnName);
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
}

