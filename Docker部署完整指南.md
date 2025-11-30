# Docker部署完整指南

## 目录结构说明

```
ruoyi-docker/
├── docker-compose.yml          # Docker Compose编排文件（核心配置文件）
├── build-jar.bat               # 后端JAR包重新打包脚本
├── 重新构建H5前端.bat           # H5前端Docker镜像重新构建脚本
│
├── backend/                    # 后端服务配置
│   ├── Dockerfile              # 后端Docker镜像构建文件
│   ├── jar/                    # JAR文件目录
│   │   └── ruoyi-admin.jar     # 后端JAR包（需要预先打包）
│   └── config/                 # 外部配置文件目录
│       └── application-prod.yml # 生产环境配置（可外部修改）
│
├── h5-ui/                      # H5移动端前端服务配置
│   ├── Dockerfile              # H5前端Docker镜像构建文件
│   ├── nginx.conf              # Nginx配置文件（API代理、CORS等）
│   └── dist/                   # H5前端静态文件目录（需要预先打包）
│       ├── index.html
│       └── static/             # 静态资源（JS、CSS、图片等）
│
├── web-ui/                     # PC管理后台前端服务配置
│   ├── Dockerfile              # PC前端Docker镜像构建文件
│   ├── nginx.conf              # Nginx配置文件
│   └── dist/                   # PC前端静态文件目录（需要预先打包）
│
├── mysql/                      # MySQL数据库配置
│   ├── conf.d/                 # MySQL配置文件目录
│   │   └── my.cnf              # MySQL自定义配置
│   └── init/                   # 数据库初始化脚本目录（按数字顺序执行）
│       ├── 01_ry_20250522.sql  # 主库（系统表）初始化
│       ├── 02_quartz.sql       # Quartz 调度表初始化
│       └── 03_dreamcampus.sql  # 业务表（最后执行）
│
└── redis/                      # Redis配置（当前为空，使用默认配置）
```

---

## 各组件作用说明

### 1. docker-compose.yml（核心配置文件）

**作用**：定义整个应用的容器编排，包括所有服务的配置、依赖关系、网络、数据卷等。

**包含的服务**：
- **ruoyi-mysql**：MySQL 8.0数据库服务（Sealos中同名Service）
- **ruoyi-redis**：Redis 7缓存服务（Sealos中同名Service）
- **ruoyi-backend**：Spring Boot后端服务
- **ruoyi-web-ui**：PC管理后台前端服务
- **ruoyi-h5-ui**：移动端H5前端服务

> 说明：docker-compose 与 Sealos 现已使用完全一致的服务命名，前端容器会在启动时自动解析 `ruoyi-backend`，无需再为不同环境手动修改 `BACKEND_HOST`。

**关键配置**：
- 服务依赖关系（depends_on）
- 健康检查（healthcheck）
- 环境变量（environment）
- 端口映射（ports）
- 数据卷（volumes）
- 网络（networks）

---

### 2. backend/（后端服务）

#### backend/Dockerfile

**作用**：定义后端Docker镜像的构建过程。

**关键步骤**：
1. 使用`eclipse-temurin:8-jre`作为基础镜像（Java 1.8）
2. 创建文件上传目录`/home/dreamcampus/uploadPath`
3. 创建配置目录`/app/config`
4. 复制JAR文件到容器
5. 复制外部配置文件（如果存在）
6. 设置JVM参数
7. 定义启动命令（支持外部配置文件）

**特点**：
- 支持外部配置文件，无需重新打包JAR即可修改配置
- 配置文件优先级：外部配置 > JAR包内配置

#### backend/jar/ruoyi-admin.jar

**作用**：后端应用的JAR包文件。

**来源**：
- 从`RuoYi-Vue-master`项目打包生成
- 使用Maven命令：`mvn clean package -Dmaven.test.skip=true`
- 打包后复制到此目录

**重新打包方法**：
```bash
# 方法1：使用提供的脚本
cd ruoyi-docker
build-jar.bat

# 方法2：手动打包
cd RuoYi-Vue-master
mvn clean package -Dmaven.test.skip=true
copy ruoyi-admin\target\ruoyi-admin.jar ruoyi-docker\backend\jar\ruoyi-admin.jar
```

#### backend/config/application-prod.yml

**作用**：生产环境的外部配置文件。

**优点**：
- 修改后无需重新打包JAR
- 直接重建Docker镜像即可生效
- 优先级高于JAR包内配置

**配置内容**：
- 文件上传路径
- 数据库连接配置（支持环境变量覆盖）
- Redis连接配置（支持环境变量覆盖）
- 日志配置

**修改方法**：
1. 直接编辑`backend/config/application-prod.yml`
2. 重新构建镜像：`docker-compose build backend`
3. 重启服务：`docker-compose up -d backend`

---

### 3. h5-ui/（H5移动端前端）

#### h5-ui/Dockerfile

**作用**：定义H5前端Docker镜像的构建过程。

**关键步骤**：
1. 使用`nginx:alpine`作为基础镜像
2. 复制静态文件到`/usr/share/nginx/html`
3. 复制Nginx配置文件
4. 暴露81端口

#### h5-ui/nginx.conf

**作用**：Nginx服务器配置，包括：
- 静态文件服务
- API请求代理（`/prod-api/` → `http://backend:8080/`）
- CORS跨域支持
- OPTIONS请求处理

**关键配置**：
```nginx
location /prod-api/ {
    proxy_pass http://backend:8080/;
    # CORS配置
    add_header Access-Control-Allow-Origin * always;
    # ...
}
```

#### h5-ui/dist/

**作用**：H5前端打包后的静态文件目录。

**来源**：
- 使用HBuilderX打开`RuoYi-App-master`项目
- 点击菜单：**发行** → **网站-H5**
- 将打包后的`dist`目录内容复制到此目录

**重要提示**：
- 打包前确保`RuoYi-App-master/config.js`中`baseUrl`为`/prod-api`
- 打包后必须重新构建Docker镜像才能生效

**重新打包流程**：
1. 使用HBuilderX打开`RuoYi-App-master`
2. 确认`config.js`中`baseUrl`为`/prod-api`
3. 点击菜单：**发行** → **网站-H5**
4. 将打包后的`dist`目录内容复制到`h5-ui/dist/`
5. 运行`重新构建H5前端.bat`或执行：
   ```bash
   docker-compose build h5-ui
   docker-compose up -d h5-ui
   ```

---

### 4. web-ui/（PC管理后台前端）

#### web-ui/Dockerfile

**作用**：定义PC前端Docker镜像的构建过程。

**结构与h5-ui类似**，使用Nginx提供静态文件服务。

#### web-ui/dist/

**作用**：PC前端打包后的静态文件目录。

**来源**：
- 从`RuoYi-Vue-master/ruoyi-ui`项目打包生成
- 使用Vue CLI命令：`npm run build:prod`
- 打包后复制到此目录

---

### 5. mysql/（MySQL数据库）

#### mysql/conf.d/my.cnf

**作用**：MySQL自定义配置文件。

**可配置项**：
- 字符集
- 时区
- 最大连接数
- 其他MySQL参数

#### mysql/init/

**作用**：数据库初始化脚本目录。

**执行时机**：MySQL容器首次启动时，会自动执行此目录下的所有`.sql`文件。

**包含的脚本（按文件名前缀顺序执行）**：
- `01_ry_20250522.sql`：主数据库表结构
- `02_quartz.sql`：定时任务数据库表结构
- `03_dreamcampus.sql`：业务数据库表结构（依赖前两个脚本的基础数据，必须最后执行）

**注意事项**：
- 脚本按文件名字母顺序执行
- 如果数据库已存在，不会重复执行
- 修改脚本后需要删除数据卷重新创建容器

---

### 6. redis/（Redis缓存）

**作用**：Redis配置目录（当前为空，使用默认配置）。

**如需自定义配置**：
- 可以在此目录创建`redis.conf`配置文件
- 在`docker-compose.yml`中挂载配置文件

---

## 部署前准备清单

### 必需文件

1. ✅ **后端JAR包**
   - 位置：`backend/jar/ruoyi-admin.jar`
   - 来源：从`RuoYi-Vue-master`项目打包

2. ✅ **PC前端静态文件**
   - 位置：`web-ui/dist/`
   - 来源：从`RuoYi-Vue-master/ruoyi-ui`项目打包

3. ✅ **H5前端静态文件**
   - 位置：`h5-ui/dist/`
   - 来源：使用HBuilderX打包`RuoYi-App-master`项目

4. ✅ **数据库初始化脚本**
   - 位置：`mysql/init/*.sql`
   - 来源：项目SQL文件

### 可选配置

1. **外部配置文件**
   - 位置：`backend/config/application-prod.yml`
   - 作用：生产环境配置（如不配置，使用JAR包内默认配置）

2. **MySQL自定义配置**
   - 位置：`mysql/conf.d/my.cnf`
   - 作用：MySQL参数调优

---

## 部署步骤

### 1. 准备后端JAR包

```bash
# 方法1：使用脚本
cd ruoyi-docker
build-jar.bat

# 方法2：手动打包
cd RuoYi-Vue-master
mvn clean package -Dmaven.test.skip=true
copy ruoyi-admin\target\ruoyi-admin.jar ruoyi-docker\backend\jar\ruoyi-admin.jar
```

### 2. 准备前端静态文件

**PC前端**：
```bash
cd RuoYi-Vue-master/ruoyi-ui
npm install
npm run build:prod
# 将dist目录内容复制到 ruoyi-docker/web-ui/dist/
```

**H5前端**：
1. 使用HBuilderX打开`RuoYi-App-master`项目
2. 确认`config.js`中`baseUrl`为`/prod-api`
3. 点击菜单：**发行** → **网站-H5**
4. 将打包后的`dist`目录内容复制到`ruoyi-docker/h5-ui/dist/`

### 3. 配置生产环境（可选）

编辑`backend/config/application-prod.yml`，配置：
- 数据库连接
- Redis连接
- 文件上传路径
- 其他生产环境参数

### 4. 启动服务

```bash
cd ruoyi-docker
docker-compose up -d --build
```

### 5. 验证服务

- **PC管理后台**：http://localhost:80
- **移动端H5**：http://localhost:81
- **后端API**：http://localhost:8080

查看日志：
```bash
docker-compose logs -f backend
docker-compose logs -f h5-ui
```

---

## 常用操作

### 重新构建单个服务

```bash
# 重新构建后端
docker-compose build backend
docker-compose up -d backend

# 重新构建H5前端
docker-compose build h5-ui
docker-compose up -d h5-ui

# 或使用提供的脚本
重新构建H5前端.bat
```

### 查看服务状态

```bash
docker-compose ps
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f h5-ui
docker-compose logs -f mysql
```

### 停止服务

```bash
docker-compose down
```

### 停止并删除数据卷（谨慎操作）

```bash
docker-compose down -v
```

---

## 环境变量配置

在`docker-compose.yml`中可以配置以下环境变量：

### 后端服务环境变量

- `SPRING_PROFILES_ACTIVE`：Spring配置文件激活（默认：prod）
- `SPRING_DATASOURCE_DRUID_MASTER_URL`：数据库连接URL
- `SPRING_DATASOURCE_DRUID_MASTER_USERNAME`：数据库用户名
- `SPRING_DATASOURCE_DRUID_MASTER_PASSWORD`：数据库密码
- `SPRING_REDIS_HOST`：Redis主机地址
- `SPRING_REDIS_PORT`：Redis端口

**注意**：环境变量优先级高于配置文件，可以用于覆盖`application-prod.yml`中的配置。

---

## 端口说明

| 服务 | 容器端口 | 主机端口 | 说明 |
|------|---------|---------|------|
| MySQL | 3306 | 3307 | 数据库服务 |
| Redis | 6379 | 6379 | 缓存服务 |
| Backend | 8080 | 8080 | 后端API服务 |
| Web-UI | 80 | 80 | PC管理后台 |
| H5-UI | 81 | 81 | 移动端H5 |

---

## 数据持久化

### MySQL数据

存储在Docker volume `mysql_data`中，删除容器不会丢失数据。

### Redis数据

存储在Docker volume `redis_data`中，删除容器不会丢失数据。

### 文件上传

后端文件上传路径：`/home/dreamcampus/uploadPath`（容器内路径）

如需持久化，可以在`docker-compose.yml`中添加volume挂载。

---

## 故障排查

### 后端服务无法启动

1. 检查JAR文件是否存在：`backend/jar/ruoyi-admin.jar`
2. 查看日志：`docker-compose logs backend`
3. 检查数据库连接配置
4. 检查Redis连接配置

### H5前端无法访问后端

1. 检查`h5-ui/dist/`目录是否有文件
2. 检查`config.js`中`baseUrl`是否为`/prod-api`
3. 检查Nginx配置是否正确
4. 查看浏览器控制台错误信息
5. 检查后端服务是否正常运行

### 数据库连接失败

1. 检查MySQL容器是否正常运行
2. 检查数据库初始化脚本是否执行成功
3. 检查连接配置中的主机名（应使用`mysql`，不是`localhost`）
4. 查看MySQL日志：`docker-compose logs mysql`

---

## 相关文档

详细部署说明请参考项目根目录下的 `Docker部署完整指南.md`

---

**文档生成时间**：2025年11月22日

