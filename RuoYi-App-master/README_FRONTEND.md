# 梦幻校园前端开发说明

## 已完成的修改

### 1. 配置修改
- ✅ 修改 `config.js` 中的 `baseUrl` 为本地后端地址：`http://localhost:8080`

### 2. 二手市场模块前端对接

#### 已修改的页面：

1. **商品列表页** (`pages/secondhand/index.vue`)
   - ✅ 调用真实接口加载商品列表
   - ✅ 调用真实接口加载分类列表
   - ✅ 支持分类筛选
   - ✅ 支持搜索
   - ✅ 支持下拉刷新

2. **商品详情页** (`pages/secondhand/detail.vue`)
   - ✅ 调用真实接口加载商品详情
   - ✅ 自动增加浏览次数
   - ✅ 显示卖家信息

3. **发布商品页** (`pages/secondhand/publish.vue`)
   - ✅ 调用真实接口加载分类列表
   - ✅ 调用真实接口提交商品数据
   - ✅ 表单验证

### 3. API接口文件
- ✅ `api/secondhand.js` - 二手市场相关接口封装

## 接口说明

### 商品列表接口
- **URL**: `GET /dreamcampus/secondhand/list`
- **参数**: 
  - `categoryId` (可选) - 分类ID
  - `goodsTitle` (可选) - 商品标题（搜索）
  - `goodsStatus` (可选) - 商品状态
- **返回**: `{code: 200, msg: "查询成功", rows: [...], total: 100}`

### 商品详情接口
- **URL**: `GET /dreamcampus/secondhand/{goodsId}`
- **返回**: `{code: 200, msg: "操作成功", data: {...}}`

### 新增商品接口
- **URL**: `POST /dreamcampus/secondhand`
- **参数**: 
  ```json
  {
    "goodsTitle": "商品标题",
    "categoryId": 1,
    "goodsPrice": 100.00,
    "goodsDesc": "商品描述",
    "goodsStatus": "0"
  }
  ```

### 分类列表接口
- **URL**: `GET /dreamcampus/secondhand/category/list`
- **返回**: `{code: 200, msg: "操作成功", data: [...]}`

## 使用说明

### 1. 启动后端服务
确保后端服务已启动在 `http://localhost:8080`

### 2. 启动前端项目
```bash
cd RuoYi-App-master
npm install
npm run dev:h5  # H5开发
# 或
npm run dev:mp-weixin  # 微信小程序
```

### 3. 测试功能
1. 打开二手市场页面，应该能看到商品列表
2. 点击商品可以查看详情
3. 点击发布按钮可以发布新商品
4. 分类筛选功能正常

## 注意事项

1. **登录状态**: 发布商品等功能需要登录，请确保已登录
2. **跨域问题**: 如果遇到跨域问题，需要在后端配置CORS
3. **图片上传**: 图片上传功能暂未实现，需要后续开发
4. **数据格式**: 后端返回的数据格式：
   - 列表接口：`{code, msg, rows, total}`
   - 详情接口：`{code, msg, data}`

## 待完善功能

- [ ] 商品图片上传
- [ ] 商品收藏功能
- [ ] 联系卖家功能
- [ ] 购买功能
- [ ] 商品编辑功能
- [ ] 商品删除功能

