const express = require('express');
const router = express.Router();
const profileController = require('../controllers/profileController');
const authenticateToken = require('../utils/authenticateToken');

// 应用鉴权中间件，所有路由都需要登录才能访问
router.use(authenticateToken);

// 获取用户个人信息
router.get('/profile', profileController.getProfile);

// 获取用户点赞的内容
router.get('/profile/likes', profileController.getLikes);

// 获取用户观看历史
router.get('/profile/view-history', profileController.getViewHistory);

// 获取用户发布的内容
router.get('/profile/contents', profileController.getContents);

module.exports = router;