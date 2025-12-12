const express = require('express');
const sequelize = require('./config/database');
const authRoutes = require('./routes/authRoutes');
const profileRoutes = require('./routes/profileRoutes');
const User = require('./models/User');
// 导入其他模型以确保关联关系被定义
const Content = require('./models/Content');
const Like = require('./models/Like');
const ViewHistory = require('./models/ViewHistory');
const Tag = require('./models/Tag');
const ContentTag = require('./models/ContentTag');
require('dotenv').config();

// 定义模型关联关系
// 1. 用户和内容的关系
User.hasMany(Content, {
  foreignKey: 'userId',
  as: 'contents'
});
Content.belongsTo(User, {
  foreignKey: 'userId',
  as: 'user'
});

// 2. 用户和点赞的关系
User.hasMany(Like, {
  foreignKey: 'userId',
  as: 'likes'
});
Like.belongsTo(User, {
  foreignKey: 'userId',
  as: 'user'
});

// 3. 内容和点赞的关系
Content.hasMany(Like, {
  foreignKey: 'contentId',
  as: 'likes'
});
Like.belongsTo(Content, {
  foreignKey: 'contentId',
  as: 'content'
});

// 4. 用户和观看历史的关系
User.hasMany(ViewHistory, {
  foreignKey: 'userId',
  as: 'viewHistories'
});
ViewHistory.belongsTo(User, {
  foreignKey: 'userId',
  as: 'user'
});

// 5. 内容和观看历史的关系
Content.hasMany(ViewHistory, {
  foreignKey: 'contentId',
  as: 'viewHistories'
});
ViewHistory.belongsTo(Content, {
  foreignKey: 'contentId',
  as: 'content'
});

// 6. 内容和标签的多对多关系
Content.belongsToMany(Tag, {
  through: ContentTag,
  foreignKey: 'contentId',
  otherKey: 'tagId',
  as: 'tags'
});
Tag.belongsToMany(Content, {
  through: ContentTag,
  foreignKey: 'tagId',
  otherKey: 'contentId',
  as: 'contents'
});

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 路由
app.use('/api/auth', authRoutes);
app.use('/api', profileRoutes);

// 测试路由
app.get('/', (req, res) => {
  res.json({
    message: '欢迎使用用户认证系统',
    version: '1.0.0'
  });
});

// 同步数据库并启动服务器
async function startServer() {
  console.log('开始启动服务器...');
  console.log('数据库配置:', {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    database: process.env.DB_NAME,
    port: process.env.DB_PORT
  });
  
  try {
    // 测试数据库连接
    console.log('正在测试数据库连接...');
    await sequelize.authenticate();
    console.log('数据库连接成功');
    
    // 同步数据库模型
    console.log('正在同步数据库模型...');
    await sequelize.sync({
      alter: true // 开发环境下自动更新表结构，生产环境建议使用 migrations
    });
    console.log('数据库同步成功');

    // 启动服务器
    console.log(`正在启动服务器，监听端口 ${PORT}...`);
    const server = app.listen(PORT, () => {
      console.log(`服务器运行在 http://localhost:${PORT}`);
      console.log(`API文档: http://localhost:${PORT}/api/auth`);
    });
    
    // 监听服务器错误
    server.on('error', (err) => {
      console.error('服务器运行错误:', err);
      process.exit(1);
    });
    
    // 监听SIGINT信号，优雅关闭服务器
    process.on('SIGINT', () => {
      console.log('收到SIGINT信号，正在关闭服务器...');
      server.close(() => {
        console.log('服务器已关闭');
        process.exit(0);
      });
    });
  } catch (error) {
    console.error('启动服务器失败:', error);
    console.error('错误堆栈:', error.stack);
    process.exit(1);
  }
}

startServer();
