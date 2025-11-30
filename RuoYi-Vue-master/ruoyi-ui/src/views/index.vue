<template>
  <div class="app-container home">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1 class="banner-title">欢迎使用梦幻校园管理系统</h1>
        <p class="banner-subtitle">Dream Campus Management System</p>
        <p class="banner-desc">专为校园生活打造的综合管理平台</p>
      </div>
    </div>

    <!-- 功能卡片 -->
    <el-row :gutter="20" class="feature-cards">
      <el-col :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="feature-card" shadow="hover" @click.native="navigateToUser">
          <div class="card-icon user-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <h3 class="card-title">用户管理</h3>
          <p class="card-desc">查看和管理所有注册用户，监控在线状态</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="feature-card" shadow="hover" @click.native="navigateToSecondhand">
          <div class="card-icon goods-icon">
            <i class="el-icon-goods"></i>
          </div>
          <h3 class="card-title">二手商品</h3>
          <p class="card-desc">审核和管理用户发布的二手商品信息</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="feature-card" shadow="hover" @click.native="navigateToErrand">
          <div class="card-icon task-icon">
            <i class="el-icon-s-order"></i>
          </div>
          <h3 class="card-title">跑腿服务</h3>
          <p class="card-desc">管理跑腿任务，确保服务质量和安全</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="feature-card" shadow="hover" @click.native="navigateToForum">
          <div class="card-icon forum-icon">
            <i class="el-icon-chat-line-round"></i>
          </div>
          <h3 class="card-title">论坛管理</h3>
          <p class="card-desc">审核论坛帖子，维护良好的交流环境</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统信息 -->
    <el-row :gutter="20" class="info-section">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="info-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-info"></i>
            <span>系统信息</span>
          </div>
          <div class="info-content">
            <div class="info-item">
              <span class="info-label">系统名称</span>
              <span class="info-value">梦幻校园管理系统</span>
            </div>
            <div class="info-item">
              <span class="info-label">当前版本</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">开发者</span>
              <span class="info-value">北洋TT</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="info-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-connection"></i>
            <span>技术架构</span>
          </div>
          <div class="tech-content">
            <div class="tech-group">
              <h4 class="tech-title">后端技术</h4>
              <div class="tech-tags">
                <el-tag size="small" type="success">SpringBoot</el-tag>
                <el-tag size="small" type="success">MyBatis</el-tag>
                <el-tag size="small" type="success">JWT</el-tag>
                <el-tag size="small" type="info">Spring Security</el-tag>
              </div>
            </div>
            <div class="tech-group">
              <h4 class="tech-title">前端技术</h4>
              <div class="tech-tags">
                <el-tag size="small" type="primary">Vue.js</el-tag>
                <el-tag size="small" type="primary">Element UI</el-tag>
                <el-tag size="small" type="primary">Vuex</el-tag>
                <el-tag size="small" type="info">Axios</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="info-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-message"></i>
            <span>联系方式</span>
          </div>
          <div class="contact-content">
            <div class="contact-item">
              <i class="el-icon-message"></i>
              <span>联系邮箱</span>
              <el-link href="mailto:beiyangtt01@gmail.com" type="primary">beiyangtt01@gmail.com</el-link>
            </div>
            <div class="contact-item">
              <i class="el-icon-s-promotion"></i>
              <span>官方网站</span>
              <el-link href="#" type="primary">梦幻校园</el-link>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "1.0.0"
    }
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank")
    },
    // 跳转到用户管理
    navigateToUser() {
      this.$router.push('/system/user').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error('路由跳转失败:', err)
        }
      })
    },
    // 跳转到二手商品管理
    navigateToSecondhand() {
      this.navigateToRoute('/dreamcampus/secondhand', '二手商品管理')
    },
    // 跳转到跑腿服务管理
    navigateToErrand() {
      this.navigateToRoute('/dreamcampus/errand', '跑腿服务管理')
    },
    // 跳转到论坛管理
    navigateToForum() {
      this.navigateToRoute('/dreamcampus/forum', '论坛管理')
    },
    // 通用路由跳转方法，带错误处理
    navigateToRoute(path, name) {
      // 从侧边栏路由中查找匹配的路由
      const findRoute = (routes, targetPath) => {
        for (const route of routes) {
          if (route.path === targetPath || route.path === targetPath.replace(/^\//, '')) {
            return route
          }
          if (route.children && route.children.length > 0) {
            const found = findRoute(route.children, targetPath)
            if (found) return found
          }
        }
        return null
      }

      const sidebarRouters = this.$store.getters.sidebarRouters || []
      const foundRoute = findRoute(sidebarRouters, path)

      if (foundRoute) {
        // 路由存在，直接跳转
        this.$router.push(path).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error('路由跳转失败:', err)
          }
        })
      } else {
        // 路由不存在，可能是还未加载，尝试跳转并提示
        this.$router.push(path).catch(() => {
          this.$message.warning(`${name}页面未找到。请检查：\n1. 菜单是否已在数据库中配置\n2. 当前用户是否有访问权限\n3. 如果已配置，请刷新页面后再试`)
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #333;
  overflow-x: hidden;

  // 欢迎横幅
  .welcome-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 60px 40px;
    margin-bottom: 30px;
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
      animation: rotate 20s linear infinite;
    }

    .banner-content {
      position: relative;
      z-index: 1;
      text-align: center;
      color: #fff;
    }

    .banner-title {
      font-size: 36px;
      font-weight: 600;
      margin: 0 0 10px 0;
      letter-spacing: 1px;
    }

    .banner-subtitle {
      font-size: 18px;
      margin: 0 0 15px 0;
      opacity: 0.9;
      font-weight: 300;
      letter-spacing: 2px;
    }

    .banner-desc {
      font-size: 16px;
      margin: 0;
      opacity: 0.85;
      font-weight: 300;
    }
  }

  @keyframes rotate {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  // 功能卡片
  .feature-cards {
    margin-bottom: 30px;

    .feature-card {
      border: none;
      border-radius: 12px;
      transition: all 0.3s ease;
      cursor: pointer;
      height: 100%;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .card-icon {
        width: 64px;
        height: 64px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 20px;
        font-size: 32px;
        color: #fff;
        transition: all 0.3s ease;

        &.user-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.goods-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.task-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.forum-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .card-title {
        font-size: 18px;
        font-weight: 600;
        margin: 0 0 10px 0;
        text-align: center;
        color: #333;
      }

      .card-desc {
        font-size: 14px;
        color: #666;
        text-align: center;
        margin: 0;
        line-height: 1.6;
      }
    }
  }

  // 信息区域
  .info-section {
    .info-card {
      border: none;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      height: 100%;

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }

      .card-header {
        display: flex;
        align-items: center;
        font-size: 16px;
        font-weight: 600;
        color: #333;

        i {
          margin-right: 8px;
          font-size: 18px;
          color: #667eea;
        }
      }

      // 系统信息
      .info-content {
        .info-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .info-label {
            font-size: 14px;
            color: #666;
            font-weight: 500;
          }

          .info-value {
            font-size: 14px;
            color: #333;
            font-weight: 600;
          }
        }
      }

      // 技术架构
      .tech-content {
        .tech-group {
          margin-bottom: 20px;

          &:last-child {
            margin-bottom: 0;
          }

          .tech-title {
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin: 0 0 12px 0;
          }

          .tech-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .el-tag {
              border-radius: 6px;
              padding: 4px 12px;
              font-size: 12px;
            }
          }
        }
      }

      // 联系方式
      .contact-content {
        .contact-item {
          display: flex;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          i {
            font-size: 18px;
            color: #667eea;
            margin-right: 12px;
            width: 20px;
          }

          span {
            font-size: 14px;
            color: #666;
            margin-right: 12px;
            min-width: 70px;
          }

          .el-link {
            font-size: 14px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .home {
    .welcome-banner {
      padding: 40px 20px;

      .banner-title {
        font-size: 28px;
      }

      .banner-subtitle {
        font-size: 16px;
      }

      .banner-desc {
        font-size: 14px;
      }
    }

    .feature-cards {
      .feature-card {
        margin-bottom: 20px;
      }
    }

    .info-section {
      .info-card {
        margin-bottom: 20px;
      }
    }
  }
}
</style>
