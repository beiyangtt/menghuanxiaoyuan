# 梦幻校园后端开发进度

## 已完成工作

### 1. 数据库设计 ✅
- 已创建完整的数据库表结构（14张表）
- SQL文件位置：`sql/03_dreamcampus.sql`

### 2. 二手市场模块后端开发 ✅

#### 实体类（Domain）
- ✅ `DcSecondhandGoods` - 二手商品实体
- ✅ `DcSecondhandCategory` - 商品分类实体

#### Mapper层
- ✅ `DcSecondhandGoodsMapper` - 商品Mapper接口
- ✅ `DcSecondhandGoodsMapper.xml` - 商品Mapper XML
- ✅ `DcSecondhandCategoryMapper` - 分类Mapper接口
- ✅ `DcSecondhandCategoryMapper.xml` - 分类Mapper XML

#### Service层
- ✅ `IDcSecondhandGoodsService` - 商品Service接口
- ✅ `DcSecondhandGoodsServiceImpl` - 商品Service实现
- ✅ `IDcSecondhandCategoryService` - 分类Service接口
- ✅ `DcSecondhandCategoryServiceImpl` - 分类Service实现

#### Controller层
- ✅ `DcSecondhandGoodsController` - 商品Controller
- ✅ `DcSecondhandCategoryController` - 分类Controller

#### 移动端API
- ✅ `api/secondhand.js` - 移动端商品API接口

### 3. 接口说明

#### 二手商品接口
- `GET /dreamcampus/secondhand/list` - 查询商品列表（支持分页、分类筛选）
- `GET /dreamcampus/secondhand/{goodsId}` - 查询商品详情（自动增加浏览次数）
- `POST /dreamcampus/secondhand` - 新增商品
- `PUT /dreamcampus/secondhand` - 修改商品（仅发布者可修改）
- `DELETE /dreamcampus/secondhand/{goodsIds}` - 删除商品（仅发布者可删除）

#### 商品分类接口
- `GET /dreamcampus/secondhand/category/list` - 查询分类列表

## 待开发模块

### 1. 二手市场模块（部分完成）
- ✅ 商品CRUD
- ⏳ 商品图片上传
- ⏳ 商品收藏功能
- ⏳ 交易订单功能

### 2. 跑腿服务模块
- ⏳ 任务CRUD
- ⏳ 接单功能
- ⏳ 任务状态管理

### 3. 论坛模块
- ⏳ 帖子CRUD
- ⏳ 评论功能
- ⏳ 点赞功能

### 4. 消息模块
- ⏳ 消息列表
- ⏳ 消息已读状态
- ⏳ 消息推送

## 使用说明

### 1. 执行数据库脚本
```sql
source sql/03_dreamcampus.sql;
```

### 2. 启动后端服务
- 确保MySQL数据库已启动
- 确保Redis已启动
- 运行 `RuoYiApplication.java`

### 3. 测试接口
- 使用Postman或Swagger测试接口
- Swagger地址：http://localhost:8080/swagger-ui/index.html

## 注意事项

1. **权限验证**：当前接口已集成北洋TT的权限验证，需要登录后才能访问
2. **用户ID**：新增商品时会自动获取当前登录用户的ID
3. **软删除**：删除操作使用软删除（del_flag = '2'），数据不会真正删除
4. **浏览次数**：查看商品详情时会自动增加浏览次数

