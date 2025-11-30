# Sealos Cloud 部署指南

## 部署概述

本指南将帮助您将RuoYi项目部署到Sealos Cloud平台，实现：
- ✅ PC管理后台通过公网访问
- ✅ 移动端APP安装后能正常使用

## 部署架构

```
┌─────────────────────────────────────────┐
│         Sealos Cloud 平台                │
│                                         │
│  ┌──────────┐  ┌──────────┐             │
│  │  MySQL   │  │  Redis   │             │
│  │  (内网)   │  │  (内网)   │             │
│  └────┬─────┘  └────┬─────┘             │
│       │             │                   │
│       └──────┬──────┘                   │
│              │                          │
│         ┌────▼─────┐                    │
│         │ Backend  │                    │
│         │  (内网)   │                    │
│         └────┬─────┘                    │
│              │                          │
│    ┌─────────┴─────────┐                │
│    │                   │                │
│ ┌──▼────┐        ┌────▼───┐             │
│ │Web-UI │        │ H5-UI  │             │
│ │(公网) │        │ (公网)  │              │
│ └───────┘        └────────┘             │
└─────────────────────────────────────────┘
         │                    │
         │                    │
    ┌────▼────┐         ┌────▼────┐
    │ PC浏览器 │         │ 移动端APP │
    └─────────┘         └─────────┘
```

## 前置准备

### 1. 准备镜像

需要将以下镜像推送到镜像仓库（Docker Hub 或 Sealos 镜像仓库）。镜像命名与 docker-compose 内部服务名称保持一致，方便在 Sealos 中直接引用：

- **后端镜像**：`your_hub_name/ruoyi-backend:latest`
- **PC前端镜像**：`your_hub_name/ruoyi-web-ui:latest`
- **H5前端镜像**：`your_hub_name/ruoyi-h5-ui:latest`

### 2. 推送镜像到Docker Hub

```bash
# 1. 登录Docker Hub
docker login

# 构建后端镜像
docker build -t your_hub_name/ruoyi-backend:latest ./backend
docker push your_hub_name/ruoyi-backend:latest

# 构建PC前端镜像（entrypoint.sh/nginx.conf 必须是仓库里的最新版）
docker build -t your_hub_name/ruoyi-web-ui:latest ./web-ui
docker push your_hub_name/ruoyi-web-ui:latest

# 构建H5前端镜像
docker build -t your_hub_name/ruoyi-h5-ui:latest ./h5-ui
docker push your_hub_name/ruoyi-h5-ui:latest
```

**重要提示**：
- 确保在构建镜像前，`web-ui`和`h5-ui`目录中的`entrypoint.sh`和`nginx.conf`文件已更新
- 如果之前已经推送过旧版本的镜像，需要重新构建并推送新版本
- 可以在本地先测试镜像是否正常工作：
  ```bash
  # 测试web-ui镜像（使用docker-compose环境变量）
  docker run -d -p 8081:80 \
    -e BACKEND_HOST=ruoyi-backend \
    -e BACKEND_PORT=8080 \
    your_hub_name/ruoyi-web-ui:latest
  
  # 测试h5-ui镜像
  docker run -d -p 8082:81 \
    -e BACKEND_HOST=ruoyi-backend \
    -e BACKEND_PORT=8080 \
    your_hub_name/ruoyi-h5-ui:latest
  ```

### 3. 修改移动端配置（重要！）

H5 版默认通过 `/prod-api` 代理后端，无需修改代码。只有在“重新打包原生 App 并发布给用户”的情况下，才需要把 `RuoYi-App-master/config.js` 中的 `baseUrl` 改成 H5 服务的公网域名，例如：

```javascript
module.exports = {
  baseUrl: 'https://ruoyi-h5-ui-xxx.sealos.run/prod-api',
  // ...
}
```

修改后需重新用 HBuilderX 打包原生 App；纯 H5 场景不需要更新 `config.js`。

---

## 部署步骤

### 第一步：部署MySQL数据库

1. **进入Sealos Cloud控制台**
   - 登录 https://cloud.sealos.io
   - 进入应用管理

2. **创建MySQL应用**
   - 点击"新建应用"
   - **基础配置**：
     - 应用名称：`ruoyi-mysql`
     - 镜像名：`mysql:8.0`
     - 部署模式：固定实例
     - 实例数：1
     - CPU：0.5 Core
     - 内存：512 M
   
   - **网络配置**：
     - 容器暴露端口：`3306`
     - **不开启公网访问**（内网服务）
   
   - **高级配置** → **环境变量**：
     ```
     MYSQL_ROOT_PASSWORD=123456
     MYSQL_DATABASE=ry-vue
     ```
   
   - **存储卷**（可选，用于数据持久化）：
     - 添加存储卷：`/var/lib/mysql`
   
   - 点击"部署应用"

3. **初始化数据库**
   
   等待MySQL启动后（约30-60秒），需要手动执行SQL初始化脚本。
   
   **方法一：通过Sealos终端执行（推荐）**
   
   1. 在Sealos控制台，找到`ruoyi-mysql`应用，点击"终端"按钮
   2. 在终端中执行以下命令连接MySQL（务必指定 `--default-character-set=utf8mb4`，否则导入后中文会乱码）：
      ```bash
      mysql --default-character-set=utf8mb4 -uroot -p123456
      ```
   3. 选择数据库：
      ```sql
      use ry-vue;
      ```
   4. 执行SQL初始化脚本（按顺序执行）：
      
      **第一步：执行主数据库脚本**
      ```sql
      source /docker-entrypoint-initdb.d/01_ry_20250522.sql;
      ```
      或者如果SQL文件不在容器中，需要手动复制SQL内容到终端执行。
      
      **第二步：执行定时任务脚本**
      ```sql
      source /docker-entrypoint-initdb.d/02_quartz.sql;
      ```
      
      **第三步：执行业务数据库脚本**
      ```sql
      source /docker-entrypoint-initdb.d/03_dreamcampus.sql;
      ```
   
   5. 验证表是否创建成功：
      ```sql
      show tables;
      ```
   6. 退出MySQL：
      ```sql
      exit;
      ```
   
   **方法二：使用mysql命令直接导入（如果SQL文件在本地）**
   
   如果SQL文件在本地，可以通过以下方式导入：
  ```bash
  # 将本地SQL文件复制到容器（需要先获取pod名称）
  kubectl cp /本地路径/01_ry_20250522.sql <命名空间>/<mysql-pod名称>:/tmp/01_ry_20250522.sql
  
  # 进入容器并导入（指定utf8mb4字符集避免乱码）
  kubectl exec -it <mysql-pod名称> -c ruoyi-mysql -- \
      mysql --default-character-set=utf8mb4 -uroot -p123456 ry-vue < /tmp/01_ry_20250522.sql
  ```
   
   

### 第二步：部署Redis缓存

1. **创建Redis应用**
   - 点击"新建应用"
   - **基础配置**：
     - 应用名称：`ruoyi-redis`
     - 镜像名：`redis:7-alpine`
     - 部署模式：固定实例
     - 实例数：1
     - CPU：0.2 Core
     - 内存：256 M
   
   - **网络配置**：
     - 容器暴露端口：`6379`
     - **不开启公网访问**（内网服务）
   
   - 点击"部署应用"

---

### 第三步：部署后端服务

1. **创建后端应用**
   - 点击"新建应用"
   - **基础配置**：
     - 应用名称：`ruoyi-backend`
     - 镜像名：`your_hub_name/ruoyi-backend:latest`
     - **镜像源**：选择"公共"（如果使用Docker Hub）
     - 部署模式：固定实例
     - 实例数：1
     - CPU：1 Core（建议）
     - 内存：1 G（建议）
   
   - **网络配置**：
     - 容器暴露端口：`8080`
     - **不开启公网访问**（内网服务，通过前端代理访问）
   
   - **高级配置** → **环境变量**：
     ```
     SPRING_PROFILES_ACTIVE=prod
     SPRING_DATASOURCE_DRUID_MASTER_URL=jdbc:mysql://ruoyi-mysql:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
     SPRING_DATASOURCE_DRUID_MASTER_USERNAME=root
     SPRING_DATASOURCE_DRUID_MASTER_PASSWORD=123456
     SPRING_REDIS_HOST=ruoyi-redis
     SPRING_REDIS_PORT=6379
     ```
   
   **注意**：
   - `ruoyi-mysql` 和 `ruoyi-redis` 是服务名称，Sealos会自动解析为内网地址
   - 确保这些服务名称与您创建的应用名称一致
   
   - 点击"部署应用"

---

### 第四步：部署PC管理后台（Web-UI）

**⚠️ 重要提示**：
- 确保已使用最新代码重新构建并推送了`ruoyi-web-ui`镜像
- 镜像必须包含支持环境变量的`entrypoint.sh`脚本
- 如果使用旧版本镜像，即使设置了环境变量也不会生效

1. **创建PC前端应用**
   - 点击"新建应用"
   - **基础配置**：
     - 应用名称：`ruoyi-web-ui`
     - 镜像名：`your_hub_name/ruoyi-web-ui:latest`
     - **镜像源**：选择"公共"（如果使用Docker Hub）
     - 部署模式：固定实例
     - 实例数：1
     - CPU：0.2 Core
     - 内存：256 M
   
   - **网络配置**：
     - 容器暴露端口：`80`
     - **✅ 开启公网访问**（重要！）
     - Sealos会自动分配公网域名，格式：`ruoyi-web-ui-xxx.sealos.run`
   
  - **高级配置** → **环境变量**（重要！）：
     ```
     BACKEND_HOST=ruoyi-backend
     BACKEND_PORT=8080
     ```
     
     **说明**：
     - `BACKEND_HOST`：后端服务名称，必须与Sealos中创建的后端应用名称一致（默认：`ruoyi-backend`）
     - `BACKEND_PORT`：后端服务端口（默认：`8080`）
     - 如未显式设置，容器会自动尝试解析`ruoyi-backend`（兼容docker-compose默认网络）
   
   - 点击"部署应用"

2. **验证PC管理后台**
   - 等待部署完成后，访问Sealos分配的公网地址
   - 例如：`https://ruoyi-web-ui-xxx.sealos.run`
   - 应该能看到登录页面

---

### 第五步：部署移动端H5前端（H5-UI）

**⚠️ 重要提示**：
- 确保已使用最新代码重新构建并推送了`ruoyi-h5-ui`镜像
- 镜像必须包含支持环境变量的`entrypoint.sh`脚本
- 如果使用旧版本镜像，即使设置了环境变量也不会生效

1. **创建H5前端应用**
   - 点击"新建应用"
   - **基础配置**：
     - 应用名称：`ruoyi-h5-ui`
     - 镜像名：`your_hub_name/ruoyi-h5-ui:latest`
     - **镜像源**：选择"公共"（如果使用Docker Hub）
     - 部署模式：固定实例
     - 实例数：1
     - CPU：0.2 Core
     - 内存：256 M
   
   - **网络配置**：
     - 容器暴露端口：`81`
     - **✅ 开启公网访问**（重要！）
     - Sealos会自动分配公网域名，格式：`ruoyi-h5-ui-xxx.sealos.run`
   
  - **高级配置** → **环境变量**（重要！）：
     ```
     BACKEND_HOST=ruoyi-backend
     BACKEND_PORT=8080
     ```
     
     **说明**：
     - `BACKEND_HOST`：后端服务名称，必须与Sealos中创建的后端应用名称一致（默认：`ruoyi-backend`）
     - `BACKEND_PORT`：后端服务端口（默认：`8080`）
     - 如未显式设置，容器会自动尝试解析`ruoyi-backend`（兼容docker-compose默认网络）
   
   - 点击"部署应用"

2. **记录H5-UI的公网地址**
   - 记录Sealos分配的公网地址，例如：`https://ruoyi-h5-ui-xxx.sealos.run`
   - 这个地址将用于移动端APP的API配置

---

## 移动端APP配置

### 修改移动端API地址

1. **编辑配置文件**
   
   编辑 `RuoYi-App-master/config.js`：
   
   ```javascript
   module.exports = {
     // 使用Sealos Cloud提供的H5-UI公网地址
     baseUrl: 'https://ruoyi-h5-ui-xxx.sealos.run/prod-api',
     
     appInfo: {
       name: "梦幻校园",
       version: "1.0.0",
       // ... 其他配置
     }
   }
   ```

2. **重新打包移动端APP**
   
   使用HBuilderX：
   - 打开项目：`RuoYi-App-master`
   - 确认`config.js`中的`baseUrl`已更新为Sealos公网地址
   - 点击菜单：**发行** → **原生App-云打包**（或本地打包）
   - 等待打包完成
   - 安装到手机测试

---

## 服务依赖关系

在Sealos Cloud中，服务之间的依赖关系：

```
MySQL (ruoyi-mysql)
  ↓
Redis (ruoyi-redis)
  ↓
Backend (ruoyi-backend) ← 依赖 MySQL 和 Redis
  ↓
Web-UI (ruoyi-web-ui) ← 代理 Backend，公网访问
H5-UI (ruoyi-h5-ui) ← 代理 Backend，公网访问
```

**服务发现**：
- Sealos Cloud中，同一命名空间的服务可以通过服务名访问
- 例如：`ruoyi-backend`可以通过`http://ruoyi-backend:8080`访问

---

## 配置检查清单

### 后端服务（ruoyi-backend）
- [ ] 镜像已推送到镜像仓库
- [ ] 环境变量配置正确（数据库连接、Redis连接）
- [ ] 服务名称与nginx配置中的名称一致
- [ ] 不开启公网访问（内网服务）

### PC管理后台（ruoyi-web-ui）
- [ ] 镜像已推送到镜像仓库
- [ ] 开启公网访问
- [ ] 环境变量配置正确（`BACKEND_HOST=ruoyi-backend`，`BACKEND_PORT=8080`）
- [ ] 后端服务名称与`BACKEND_HOST`环境变量一致

### H5移动端（ruoyi-h5-ui）
- [ ] 镜像已推送到镜像仓库
- [ ] 开启公网访问
- [ ] 环境变量配置正确（`BACKEND_HOST=ruoyi-backend`，`BACKEND_PORT=8080`）
- [ ] 后端服务名称与`BACKEND_HOST`环境变量一致
- [ ] 记录公网地址用于移动端配置

### 移动端APP
- [ ] `config.js`中的`baseUrl`已更新为H5-UI的公网地址
- [ ] 已重新打包APP
- [ ] 已安装到手机测试

---

## 常见问题

### 1. 后端无法连接MySQL/Redis

**现象**：
```
java.net.UnknownHostException: ruoyi-mysql: Name or service not known
```

**原因**：Service未创建或Service选择器不匹配

**解决步骤**：

1. **检查Service是否存在**：
   ```bash
   kubectl get svc | grep -E "(mysql|redis)"
   ```

2. **如果Service不存在，创建Service**：
   ```bash
   # 先查看MySQL Pod的标签
   kubectl get pod <mysql-pod-name> --show-labels
   
   # 创建MySQL Service（根据实际标签修改selector）
   kubectl apply -f - <<EOF
   apiVersion: v1
   kind: Service
   metadata:
     name: ruoyi-mysql
     namespace: <your-namespace>
   spec:
     selector:
       app: ruoyi-mysql  # 根据实际标签修改
     ports:
       - protocol: TCP
         port: 3306
         targetPort: 3306
   EOF
   
   # 创建Redis Service
   kubectl apply -f - <<EOF
   apiVersion: v1
   kind: Service
   metadata:
     name: ruoyi-redis
     namespace: <your-namespace>
   spec:
     selector:
       app: ruoyi-redis  # 根据实际标签修改
     ports:
       - protocol: TCP
         port: 6379
         targetPort: 6379
   EOF
   ```

3. **验证Service和Endpoints**：
   ```bash
   kubectl get svc ruoyi-mysql ruoyi-redis
   kubectl get endpoints ruoyi-mysql ruoyi-redis
   ```

4. **重启后端服务**：
   ```bash
   kubectl rollout restart deployment ruoyi-backend
   ```

**详细说明**：如遇到Service问题，请检查应用名称是否正确，确保所有服务在同一命名空间中

### 2. PC管理后台无法访问后端API

**原因**：环境变量配置不正确、后端服务名称不匹配或nginx配置未正确生成

**解决步骤**：

1. **检查环境变量配置**：
   - 在Sealos控制台，找到`ruoyi-web-ui`应用
   - 进入"环境变量"配置
   - 检查环境变量：`BACKEND_HOST`和`BACKEND_PORT`
   - 确保`BACKEND_HOST=ruoyi-backend`（与后端应用名称一致）
   - 确保`BACKEND_PORT=8080`

2. **检查后端服务名称**：
   - 确认后端应用名称是否为`ruoyi-backend`
   - 如果后端应用名称不同，需要修改`BACKEND_HOST`环境变量

3. **查看容器日志，检查nginx配置是否正确生成**：
   ```bash
   # 在Sealos控制台，找到ruoyi-web-ui应用，点击"日志"
   # 或者使用kubectl查看日志
   kubectl logs <ruoyi-web-ui-pod-name>
   ```
   
   日志中应该显示：
   ```
   正在生成nginx配置...
   BACKEND_HOST=ruoyi-backend
   BACKEND_PORT=8080
   生成的nginx配置：
   ...
   proxy_pass http://ruoyi-backend:8080/;
   ...
   测试nginx配置...
   nginx: configuration file /etc/nginx/nginx.conf test is successful
   ```

4. **如果nginx配置未正确生成，检查启动脚本**：
   - 进入容器终端，检查生成的配置文件：
     ```bash
     # 在Sealos控制台，找到ruoyi-web-ui应用，点击"终端"
     cat /etc/nginx/conf.d/default.conf
     ```
   - 确认`proxy_pass`中的地址是否正确：
     ```nginx
     proxy_pass http://ruoyi-backend:8080/;
     ```
   - 如果还是`${BACKEND_HOST}`，说明环境变量替换失败

5. **测试后端服务连接**：
   ```bash
   # 在容器终端中测试
   wget -O- http://ruoyi-backend:8080/ 2>&1
   # 或者
   curl http://ruoyi-backend:8080/
   ```

6. **重启前端服务**：
   - 修改环境变量后，需要重启`ruoyi-web-ui`应用才能生效
   - 在Sealos控制台，点击"重启"按钮

### 3. H5-UI无法访问后端API

**原因**：与Web-UI相同的问题，环境变量配置不正确或nginx配置未正确生成

**解决**：
- 参考"PC管理后台无法访问后端API"的解决步骤
- 检查`ruoyi-h5-ui`应用的环境变量配置
- 确保`BACKEND_HOST=ruoyi-backend`，`BACKEND_PORT=8080`
- 查看容器日志，确认nginx配置是否正确生成

### 4. 移动端APP无法连接后端

**原因**：`config.js`中的`baseUrl`配置错误或H5-UI服务异常

**解决**：
1. **检查H5-UI服务状态**：
   - 在Sealos控制台确认`ruoyi-h5-ui`应用正常运行
   - 访问H5-UI的公网地址，确认可以正常访问

2. **检查API地址配置**：
   - 确认`config.js`中`baseUrl`为H5-UI的公网地址：`https://ruoyi-h5-ui-xxx.sealos.run/prod-api`
   - 确保已重新打包APP

3. **测试API连接**：
   - 在浏览器中访问：`https://ruoyi-h5-ui-xxx.sealos.run/prod-api/` 
   - 应该能看到后端API的响应（或错误信息，但至少能连接）

### 5. 服务启动顺序问题

**解决**：
- 先启动MySQL和Redis
- 等待它们完全启动后，再启动Backend
- 最后启动Web-UI和H5-UI

### 6. 容器连接错误：`container not found ("ruoyi-backend")`

**错误信息**：
```
error: unable to upgrade connection: container not found ("ruoyi-backend")
```

**原因分析**：
1. **容器名称不匹配**：Sealos中容器的实际名称可能与应用名称不同
2. **容器还未启动**：Pod已创建但容器还在启动中
3. **使用了错误的容器名称**：应该使用Pod中实际的容器名称

**解决方法**：

**方法1：查看Pod中的实际容器名称**
```bash
# 查看Pod的详细信息，找到容器名称
kubectl describe pod ruoyi-backend-574b8787c9-wfk7p

# 或者查看Pod的YAML配置
kubectl get pod ruoyi-backend-574b8787c9-wfk7p -o yaml | grep -A 5 "containers:"
```

**方法2：不指定容器名称，让kubectl自动选择**
```bash
# 不指定 -c 参数，kubectl会自动选择第一个容器
kubectl exec -it ruoyi-backend-574b8787c9-wfk7p -- sh
```

**方法3：使用Sealos控制台的终端功能**
- 在Sealos控制台，找到`ruoyi-backend`应用
- 点击"终端"按钮（Sealos会自动处理容器名称）
- 这是最简单可靠的方法

**方法4：检查容器状态**
```bash
# 查看Pod状态
kubectl get pods | grep ruoyi-backend

# 查看Pod详细信息
kubectl describe pod ruoyi-backend-574b8787c9-wfk7p

# 查看容器日志
kubectl logs ruoyi-backend-574b8787c9-wfk7p
```

**推荐做法**：
- ✅ 使用Sealos控制台的终端功能（最简单）
- ✅ 如果不指定容器名称，使用 `kubectl exec -it <pod-name> -- sh`
- ❌ 避免手动指定容器名称，除非确定容器名称正确

---

## 优化建议

### 1. 使用Sealos数据库服务（可选）

如果Sealos提供MySQL和Redis的托管服务，可以使用托管服务替代自建：
- 更稳定
- 自动备份
- 更易管理

### 2. 配置自定义域名

- 在Sealos中配置自定义域名指向Web-UI和H5-UI
- 例如：`admin.yourdomain.com` 和 `h5.yourdomain.com`

### 3. 启用HTTPS

- Sealos Cloud通常自动提供HTTPS证书
- 确保使用HTTPS地址配置移动端

### 4. 资源监控

- 在Sealos控制台监控各服务的资源使用情况
- 根据实际使用情况调整CPU和内存配置

---

## 部署后验证

### 1. 验证PC管理后台
- 访问：`https://ruoyi-web-ui-xxx.sealos.run`
- 应该能看到登录页面
- 尝试登录，验证后端API连接

### 2. 验证H5前端
- 访问：`https://ruoyi-h5-ui-xxx.sealos.run`
- 应该能看到移动端界面
- 检查浏览器控制台，确认API请求正常

### 3. 验证移动端APP
- 安装打包后的APP到手机
- 打开APP，检查是否能正常加载
- 尝试登录，验证API连接
- 测试各项功能

---

## 快速部署脚本（参考）

如果需要批量部署，可以使用Sealos CLI或API。以下是手动部署的步骤总结：

1. **推送镜像** → Docker Hub
2. **部署MySQL** → 内网服务，端口3306
3. **部署Redis** → 内网服务，端口6379
4. **部署Backend** → 内网服务，端口8080，配置环境变量
5. **部署Web-UI** → 公网服务，端口80
6. **部署H5-UI** → 公网服务，端口81
7. **修改移动端配置** → 更新baseUrl为H5-UI公网地址
8. **重新打包APP** → 使用HBuilderX打包
9. **测试验证** → 验证所有功能正常

---

**文档生成时间**：2025年11月22日

**注意事项**：
- 确保所有服务在同一命名空间中，才能通过服务名互相访问
- 公网地址可能会变化，建议配置自定义域名
- 定期备份数据库数据

