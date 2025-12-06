# 用户认证系统 API

使用 Node.js + Express 实现的注册登录接口，包含完整的安全机制和数据验证。

## 技术栈

- **Node.js** - 运行环境
- **Express** - Web 框架
- **Sequelize** - ORM 数据库操作
- **MySQL** - 数据库
- **bcrypt** - 密码加密
- **JSON Web Token (JWT)** - 身份验证
- **Joi** - 请求参数验证

## 功能特性

### 注册
- 接收 `username`、`password`、`email`
- 密码使用 bcrypt 加密
- 校验用户名和邮箱唯一性
- 返回成功状态和用户信息

### 登录
- 接收 `username` + `password`
- 验证通过后生成 JWT（过期时间 2 小时）
- 返回 token 和用户信息

### 安全措施
- 防 SQL 注入（使用 Sequelize ORM）
- 请求参数校验（使用 Joi）
- 密码加密存储
- JWT 身份验证

## 安装和运行

### 环境要求
- Node.js >= 18.0.0
- MySQL >= 5.7

### 步骤

1. **克隆或下载项目**

2. **安装依赖**
   ```bash
   npm install
   ```

3. **配置环境变量**
   - 复制 `.env` 文件
   - 修改 `.env` 文件中的数据库配置和 JWT 密钥
   ```
   # 数据库配置
   DB_HOST=localhost
   DB_USER=root
   DB_PASSWORD=your_password
   DB_NAME=auth_system
   DB_PORT=3306
   
   # JWT 配置
   JWT_SECRET=your_jwt_secret_key
   JWT_EXPIRES_IN=2h
   ```

4. **启动 MySQL 服务**
   - 确保 MySQL 服务正在运行
   - 创建名为 `auth_system` 的数据库（如果不存在）

5. **运行项目**
   ```bash
   npm start
   ```
   或
   ```bash
   npm run dev
   ```

6. **测试 API**
   - 注册：`POST http://localhost:3000/api/auth/register`
   - 登录：`POST http://localhost:3000/api/auth/login`

## API 文档

### 注册接口

**URL**: `/api/auth/register`
**方法**: `POST`
**请求体**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

**成功响应**:
```json
{
  "success": true,
  "message": "注册成功",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

### 登录接口

**URL**: `/api/auth/login`
**方法**: `POST`
**请求体**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**成功响应**:
```json
{
  "success": true,
  "message": "登录成功",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

## 在 VS Code 中运行

1. **打开项目**
   - 在 VS Code 中选择 `文件` > `打开文件夹`
   - 选择项目目录 `node_auth_system`

2. **安装依赖**
   - 打开终端：`查看` > `终端`
   - 运行 `npm install`

3. **配置环境变量**
   - 在 VS Code 中打开 `.env` 文件
   - 修改数据库连接信息和 JWT 密钥

4. **启动项目**
   - 在终端中运行 `npm start`
   - 查看终端输出，确认服务器是否成功启动

5. **测试 API**
   - 可以使用 VS Code 插件如 `Thunder Client` 或 `REST Client` 来测试 API
   - 或使用 Postman 等工具

## 项目结构

```
node_auth_system/
├── config/
│   └── database.js          # 数据库配置
├── controllers/
│   └── authController.js    # 认证控制器
├── models/
│   └── User.js              # 用户模型
├── routes/
│   └── authRoutes.js        # 认证路由
├── utils/
│   └── validators.js        # 验证规则
├── .env                     # 环境变量
├── app.js                   # 主应用文件
├── package.json             # 项目配置
└── README.md                # 项目说明
```

## 开发说明

- 开发环境下使用 `sequelize.sync({ alter: true })` 自动更新表结构
- 生产环境建议使用 Sequelize Migrations 管理数据库变更
- JWT 密钥和数据库密码等敏感信息应妥善保管

## 许可证

ISC
