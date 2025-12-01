package com.virtuallive.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * 数据库结构检查工具
 * 启动时自动打印数据库表结构信息
 * 使用后请删除此类或注释 @Component 注解
 */
// @Component  // 取消注释以启用
public class DatabaseStructureChecker implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("\n=== 数据库表结构检查 ===\n");

            // 获取所有表
            ResultSet tables = metaData.getTables("virtuallive_dev", null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("表名: " + tableName);
                System.out.println("----------------------------------------");

                // 获取表的列信息
                ResultSet columns = metaData.getColumns("virtuallive_dev", null, tableName, null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getString("IS_NULLABLE");
                    String autoIncrement = columns.getString("IS_AUTOINCREMENT");

                    System.out.printf("  %-25s %-15s (%d) nullable=%s auto=%s%n",
                        columnName, columnType, columnSize, nullable, autoIncrement);
                }

                // 获取主键信息
                ResultSet primaryKeys = metaData.getPrimaryKeys("virtuallive_dev", null, tableName);
                System.out.println("\n  主键:");
                while (primaryKeys.next()) {
                    System.out.println("    " + primaryKeys.getString("COLUMN_NAME"));
                }

                // 获取外键信息
                ResultSet foreignKeys = metaData.getImportedKeys("virtuallive_dev", null, tableName);
                System.out.println("\n  外键:");
                while (foreignKeys.next()) {
                    String fkColumn = foreignKeys.getString("FKCOLUMN_NAME");
                    String pkTable = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");
                    System.out.printf("    %s -> %s.%s%n", fkColumn, pkTable, pkColumn);
                }

                System.out.println("\n========================================\n");
            }
        }
    }
}

