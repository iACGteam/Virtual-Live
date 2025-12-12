const { Op } = require('sequelize');
const User = require('../models/User');
const Content = require('../models/Content');
const Like = require('../models/Like');
const ViewHistory = require('../models/ViewHistory');

// 获取用户个人信息
exports.getProfile = async (req, res) => {
  try {
    const user = await User.findByPk(req.user.userId, {
      attributes: ['id', 'username', 'email', 'avatar', 'nickname', 'bio', 'followersCount', 'followingCount']
    });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: '用户不存在'
      });
    }

    return res.status(200).json({
      success: true,
      data: user
    });
  } catch (error) {
    console.error('获取个人信息错误:', error);
    return res.status(500).json({
      success: false,
      message: '服务器内部错误'
    });
  }
};

// 获取用户点赞的内容
exports.getLikes = async (req, res) => {
  try {
    const { page = 1, limit = 10 } = req.query;
    const offset = (page - 1) * limit;

    const likes = await Like.findAll({
      where: { userId: req.user.userId },
      include: [
        {
          model: Content,
          as: 'content',
          include: [
            {
              model: User,
              as: 'user',
              attributes: ['id', 'username', 'avatar']
            },
            {
              model: require('../models/Tag'),
              as: 'tags',
              attributes: ['id', 'name'],
              through: { attributes: [] }
            }
          ]
        }
      ],
      order: [['createdAt', 'DESC']],
      limit: parseInt(limit),
      offset: parseInt(offset)
    });

    // 获取总数
    const total = await Like.count({ where: { userId: req.user.userId } });

    return res.status(200).json({
      success: true,
      data: {
        items: likes.map(like => ({
          ...like.content.toJSON(),
          likedAt: like.createdAt
        })),
        pagination: {
          total,
          page: parseInt(page),
          limit: parseInt(limit),
          totalPages: Math.ceil(total / limit)
        }
      }
    });
  } catch (error) {
    console.error('获取点赞内容错误:', error);
    return res.status(500).json({
      success: false,
      message: '服务器内部错误'
    });
  }
};

// 获取用户观看历史
exports.getViewHistory = async (req, res) => {
  try {
    const { page = 1, limit = 10 } = req.query;
    const offset = (page - 1) * limit;
    
    // 计算30天前的日期
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

    const viewHistories = await ViewHistory.findAll({
      where: {
        userId: req.user.userId,
        viewedAt: { [Op.gte]: thirtyDaysAgo }
      },
      include: [
        {
          model: Content,
          as: 'content',
          include: [
            {
              model: User,
              as: 'user',
              attributes: ['id', 'username', 'avatar']
            },
            {
              model: require('../models/Tag'),
              as: 'tags',
              attributes: ['id', 'name'],
              through: { attributes: [] }
            }
          ]
        }
      ],
      order: [['viewedAt', 'DESC']],
      limit: parseInt(limit),
      offset: parseInt(offset)
    });

    // 获取总数
    const total = await ViewHistory.count({
      where: {
        userId: req.user.userId,
        viewedAt: { [Op.gte]: thirtyDaysAgo }
      }
    });

    return res.status(200).json({
      success: true,
      data: {
        items: viewHistories.map(history => ({
          ...history.content.toJSON(),
          viewedAt: history.viewedAt
        })),
        pagination: {
          total,
          page: parseInt(page),
          limit: parseInt(limit),
          totalPages: Math.ceil(total / limit)
        }
      }
    });
  } catch (error) {
    console.error('获取观看历史错误:', error);
    return res.status(500).json({
      success: false,
      message: '服务器内部错误'
    });
  }
};

// 获取用户发布的内容
exports.getContents = async (req, res) => {
  try {
    const { page = 1, limit = 10 } = req.query;
    const offset = (page - 1) * limit;

    const contents = await Content.findAndCountAll({
      where: { userId: req.user.userId },
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'avatar']
        },
        {
          model: require('../models/Tag'),
          as: 'tags',
          attributes: ['id', 'name'],
          through: { attributes: [] }
        }
      ],
      order: [['createdAt', 'DESC']],
      limit: parseInt(limit),
      offset: parseInt(offset)
    });

    return res.status(200).json({
      success: true,
      data: {
        items: contents.rows,
        pagination: {
          total: contents.count,
          page: parseInt(page),
          limit: parseInt(limit),
          totalPages: Math.ceil(contents.count / limit)
        }
      }
    });
  } catch (error) {
    console.error('获取发布内容错误:', error);
    return res.status(500).json({
      success: false,
      message: '服务器内部错误'
    });
  }
};
