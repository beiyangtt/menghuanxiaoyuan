# 梦幻校园移动端（UniApp）

基于 UniApp 开发的移动端应用，支持 H5、小程序、APP 多端。

## 项目说明

本项目是梦幻校园项目的移动端前端，基于 UniApp 框架开发。

## 技术栈

- **框架**：UniApp
- **UI组件**：uni-ui
- **状态管理**：Vuex
- **网络请求**：封装的自定义请求工具

## 功能模块

- ✅ 用户登录/注册
- ✅ 二手市场
- ✅ 跑腿服务
- ✅ 论坛交流
- ✅ 消息通知
- ✅ 个人中心

## 开发说明

详细开发说明请查看 [README_FRONTEND.md](./README_FRONTEND.md)

## 配置说明

在 `config.js` 中配置后端API地址：

```javascript
module.exports = {
  baseUrl: 'http://localhost:8080', // 开发环境
  // baseUrl: '/prod-api', // 生产环境（通过nginx代理）
}
```

## 运行项目

```bash
# 安装依赖
npm install

# H5开发
npm run dev:h5

# 微信小程序开发
npm run dev:mp-weixin

# 打包H5
npm run build:h5

# 打包微信小程序
npm run build:mp-weixin
```

## 项目结构

```
├── pages/          # 页面文件
├── api/            # API接口封装
├── utils/          # 工具函数
├── static/         # 静态资源
├── store/          # 状态管理
├── components/     # 公共组件
└── config.js       # 配置文件
```

## 注意事项

1. 开发前请先启动后端服务
2. 生产环境部署时，请确保 `config.js` 中的 `baseUrl` 配置正确
3. 使用 HBuilderX 可以更方便地开发和打包
