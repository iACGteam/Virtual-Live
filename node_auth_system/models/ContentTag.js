const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const ContentTag = sequelize.define('ContentTag', {
  contentId: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'Contents',
      key: 'id'
    },
    onDelete: 'CASCADE'
  },
  tagId: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'Tags',
      key: 'id'
    },
    onDelete: 'CASCADE'
  }
}, {
  timestamps: false,
  indexes: [
    {
      unique: true,
      fields: ['contentId', 'tagId']
    }
  ]
});

module.exports = ContentTag;