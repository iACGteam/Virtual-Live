const Joi = require('joi');

// 注册验证规则
exports.registerValidator = Joi.object({
  username: Joi.string()
    .min(3)
    .max(30)
    .required()
    .messages({
      'string.base': '用户名必须是字符串',
      'string.min': '用户名长度不能少于3个字符',
      'string.max': '用户名长度不能超过30个字符',
      'any.required': '用户名是必填项'
    }),
  email: Joi.string()
    .email()
    .required()
    .messages({
      'string.base': '邮箱必须是字符串',
      'string.email': '请输入有效的邮箱地址',
      'any.required': '邮箱是必填项'
    }),
  password: Joi.string()
    .min(6)
    .max(100)
    .required()
    .messages({
      'string.base': '密码必须是字符串',
      'string.min': '密码长度不能少于6个字符',
      'string.max': '密码长度不能超过100个字符',
      'any.required': '密码是必填项'
    })
});

// 登录验证规则
exports.loginValidator = Joi.object({
  username: Joi.string()
    .required()
    .messages({
      'string.base': '用户名必须是字符串',
      'any.required': '用户名是必填项'
    }),
  password: Joi.string()
    .required()
    .messages({
      'string.base': '密码必须是字符串',
      'any.required': '密码是必填项'
    })
});
