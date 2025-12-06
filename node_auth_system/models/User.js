const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const bcrypt = require('bcrypt');

const User = sequelize.define('User', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
    allowNull: false
  },
  username: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
    validate: {
      notEmpty: true,
      len: [3, 30]
    }
  },
  email: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
    validate: {
      notEmpty: true,
      isEmail: true
    }
  },
  password: {
    type: DataTypes.STRING,
    allowNull: false,
    validate: {
      notEmpty: true,
      len: [6, 100]
    }
  },
  // 个人信息字段
  avatar: {
    type: DataTypes.STRING,
    allowNull: true,
    defaultValue: 'https://via.placeholder.com/150'
  },
  nickname: {
    type: DataTypes.STRING,
    allowNull: true,
    defaultValue: DataTypes.UUIDV4
  },
  bio: {
    type: DataTypes.TEXT,
    allowNull: true,
    defaultValue: ''
  },
  followersCount: {
    type: DataTypes.INTEGER,
    allowNull: false,
    defaultValue: 0
  },
  followingCount: {
    type: DataTypes.INTEGER,
    allowNull: false,
    defaultValue: 0
  }
}, {
  hooks: {
    // 在创建用户之前加密密码
    beforeCreate: async (user) => {
      if (user.password) {
        const saltRounds = 10;
        user.password = await bcrypt.hash(user.password, saltRounds);
      }
    },
    // 在更新用户之前如果密码改变了则重新加密
    beforeUpdate: async (user) => {
      if (user.changed('password')) {
        const saltRounds = 10;
        user.password = await bcrypt.hash(user.password, saltRounds);
      }
    }
  }
});

// 比较密码方法
User.prototype.comparePassword = async function(candidatePassword) {
  return await bcrypt.compare(candidatePassword, this.password);
};



module.exports = User;
