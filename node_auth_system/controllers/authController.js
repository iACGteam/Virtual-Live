const User = require('../models/User');
const { registerValidator, loginValidator } = require('../utils/validators');
const jwt = require('jsonwebtoken');
require('dotenv').config();

// 注册控制器
exports.register = async (req, res) => {
  try {
    // 1. 验证请求参数
    const { error } = registerValidator.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        message: error.details[0].message
      });
    }

    // 2. 检查用户名是否已存在
    const existingUsername = await User.findOne({ where: { username: req.body.username } });
    if (existingUsername) {
      return res.status(400).json({
        success: false,
        message: '用户名已存在'
      });
    }

    // 3. 检查邮箱是否已存在
    const existingEmail = await User.findOne({ where: { email: req.body.email } });
    if (existingEmail) {
      return res.status(400).json({
        success: false,
        message: '邮箱已存在'
      });
    }

    // 4. 创建新用户
    const user = await User.create({
      username: req.body.username,
      email: req.body.email,
      password: req.body.password
    });

    // 5. 返回成功响应
    return res.status(201).json({
      success: true,
      message: '注册成功',
      user: {
        id: user.id,
        username: user.username,
        email: user.email
      }
    });
  } catch (error) {
    console.error('注册错误:', error);
    return res.status(500).json({
      success: false,
      message: '服务器内部错误'
    });
  }
};

// 登录控制器
exports.login = async (req, res) => {
  try {
    // 1. 验证请求参数
    const { error } = loginValidator.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        message: error.details[0].message
      });
    }

    // 2. 检查用户是否存在
    const user = await User.findOne({ where: { username: req.body.username } });
    if (!user) {
      return res.status(401).json({
        success: false,
        message: '用户名或密码错误'
      });
    }

    // 3. 验证密码
    const isPasswordValid = await user.comparePassword(req.body.password);
    if (!isPasswordValid) {
      return res.status(401).json({
        success: false,
        message: '用户名或密码错误'
      });
    }

    // 4. 生成 JWT
    const token = jwt.sign(
      { userId: user.id, username: user.username },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN }
    );

    // 5. 返回成功响应
    return res.status(200).json({
      success: true,
      message: '登录成功',
      token: token,
      user: {
        id: user.id,
        username: user.username,
        email: user.email
      }
    });
  } catch (error) {
    console.error('登录错误:', error);
    return res.status(500).json({
      success: false,
      message: '服务器内部错误'
    });
  }
};
