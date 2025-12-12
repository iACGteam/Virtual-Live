const jwt = require('jsonwebtoken');
require('dotenv').config();

// JWT 鉴权中间件
const authenticateToken = (req, res, next) => {
  // 从请求头获取 token
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({
      success: false,
      message: '未授权访问，缺少令牌'
    });
  }

  try {
    // 验证 token
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    // 将用户信息存储到请求对象中
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(403).json({
      success: false,
      message: '无效的令牌'
    });
  }
};

module.exports = authenticateToken;