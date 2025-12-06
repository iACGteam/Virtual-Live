const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const ViewHistory = sequelize.define('ViewHistory', {
  userId: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'Users',
      key: 'id'
    },
    onDelete: 'CASCADE'
  },
  contentId: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'Contents',
      key: 'id'
    },
    onDelete: 'CASCADE'
  },
  viewedAt: {
    type: DataTypes.DATE,
    allowNull: false,
    defaultValue: DataTypes.NOW
  }
}, {
  indexes: [
    {
      fields: ['userId', 'viewedAt']
    }
  ]
});



module.exports = ViewHistory;