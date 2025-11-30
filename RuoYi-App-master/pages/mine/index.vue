<template>
  <view class="mine-container" :style="{height: `${windowHeight}px`}">
    <!--顶部个人信息栏-->
    <view class="header-section">
      <view class="flex padding justify-between">
        <view class="flex align-center">
          <view v-if="!avatar" class="cu-avatar xl round bg-white">
            <view class="iconfont icon-people text-gray icon"></view>
          </view>
          <image v-if="avatar" @click="handleToAvatar" :src="avatar" class="cu-avatar xl round" mode="widthFix">
          </image>
          <view v-if="!name" @click="handleToLogin" class="login-tip">
            点击登录
          </view>
          <view v-if="name" @click="handleToInfo" class="user-info">
            <view class="u_title">
              {{ name }}
            </view>
          </view>
        </view>
        <view @click="handleToInfo" class="flex align-center">
          <text>个人信息</text>
          <view class="iconfont icon-right"></view>
        </view>
      </view>
    </view>

    <view class="content-section">
      <view class="mine-actions grid col-4 text-center">
        <view class="action-item" @click="handleFavorite">
          <view class="icon-wrapper">
            <uni-icons type="star" :size="28" color="#ff69b4"></uni-icons>
          </view>
          <text class="text">收藏</text>
        </view>
        <view class="action-item" @click="handleOrder">
          <view class="icon-wrapper">
            <uni-icons type="list" :size="28" color="#409EFF"></uni-icons>
          </view>
          <text class="text">订单</text>
        </view>
        <view class="action-item" @click="handleService">
          <view class="icon-wrapper">
            <uni-icons type="chatbubble" :size="28" color="#9c27b0"></uni-icons>
          </view>
          <text class="text">客服</text>
        </view>
        <view class="action-item" @click="handleMore">
          <view class="icon-wrapper">
            <uni-icons type="more-filled" :size="28" color="#67C23A"></uni-icons>
          </view>
          <text class="text">更多</text>
        </view>
      </view>

      <view class="menu-list">
        <view class="list-cell list-cell-arrow" @click="handleToEditInfo">
          <view class="menu-item-box">
            <view class="iconfont icon-user menu-icon"></view>
            <view>编辑资料</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleHelp">
          <view class="menu-item-box">
            <view class="iconfont icon-help menu-icon"></view>
            <view>常见问题</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleAbout">
          <view class="menu-item-box">
            <view class="iconfont icon-aixin menu-icon"></view>
            <view>关于我们</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleToSetting">
          <view class="menu-item-box">
            <view class="iconfont icon-setting menu-icon"></view>
            <view>应用设置</view>
          </view>
        </view>
      </view>

    </view>
  </view>
</template>

<script>
  import { getFavoriteCount, getMyGoodsCount } from '@/api/secondhand'
  import { updateTabBarBadge } from '@/utils/tabbar'
  
  export default {
    data() {
      return {
        name: this.$store.state.user.name,
        favoriteCount: 0,
        orderCount: 0
      }
    },
    computed: {
      avatar() {
        return this.$store.state.user.avatar
      },
      windowHeight() {
        return uni.getSystemInfoSync().windowHeight - 50
      }
    },
    onLoad() {
      this.loadCounts()
    },
    onShow() {
      // 每次显示页面时刷新数量
      this.loadCounts()
      // 更新底部导航栏消息徽章
      updateTabBarBadge()
    },
    methods: {
      handleToInfo() {
        this.$tab.navigateTo('/pages/mine/info/index')
      },
      handleToEditInfo() {
        this.$tab.navigateTo('/pages/mine/info/edit')
      },
      handleToSetting() {
        this.$tab.navigateTo('/pages/mine/setting/index')
      },
      handleToLogin() {
        this.$tab.reLaunch('/pages/login')
      },
      handleToAvatar() {
        this.$tab.navigateTo('/pages/mine/avatar/index')
      },
      handleHelp() {
        this.$tab.navigateTo('/pages/mine/help/index')
      },
      handleAbout() {
        this.$tab.navigateTo('/pages/mine/about/index')
      },
      handleFavorite() {
        this.$tab.navigateTo('/pages/mine/favorite/index')
      },
      handleOrder() {
        this.$tab.navigateTo('/pages/mine/order/index')
      },
      async loadCounts() {
        try {
          // 加载收藏数量
          const favoriteRes = await getFavoriteCount()
          if (favoriteRes.code === 200) {
            this.favoriteCount = favoriteRes.data || 0
          }
          
          // 加载订单数量（我发布的商品数量）
          const goodsRes = await getMyGoodsCount()
          if (goodsRes.code === 200) {
            this.orderCount = goodsRes.data || 0
          }
        } catch (error) {
          console.error('加载数量失败:', error)
        }
      },
      handleService() {
        uni.showModal({
          title: '联系客服',
          content: '请联系管理员：beiyangtt01@gmai.com',
          showCancel: false,
          confirmText: '知道了'
        })
      },
      handleMore() {
        uni.showToast({
          title: '叫爸爸',
          icon: 'none',
          duration: 2000
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  page {
    background-color: #f5f6f7;
  }

  .mine-container {
    width: 100%;
    height: 100%;


    .header-section {
      padding: 15px 15px 45px 15px;
      background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
      color: #333;

      .login-tip {
        font-size: 18px;
        margin-left: 10px;
      }

      .cu-avatar {
        border: 2px solid #eaeaea;

        .icon {
          font-size: 40px;
        }
      }

      .user-info {
        margin-left: 15px;

        .u_title {
          font-size: 18px;
          line-height: 30px;
        }
      }
    }

    .content-section {
      position: relative;
      top: -50px;

      .mine-actions {
        margin: 15px 15px;
        padding: 20px 0px;
        border-radius: 8px;
        background-color: white;

        .action-item {
          position: relative;
          
          .icon-wrapper {
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 56rpx;
            height: 56rpx;
            margin: 0 auto;
          }

          .text {
            display: block;
            font-size: 13px;
            margin: 8px 0px;
          }
          
          .badge {
            position: absolute;
            top: -8px;
            right: -8px;
            background-color: #f56c6c;
            color: #fff;
            font-size: 18rpx;
            min-width: 32rpx;
            height: 32rpx;
            line-height: 32rpx;
            text-align: center;
            border-radius: 16rpx;
            padding: 0 8rpx;
            box-sizing: border-box;
            font-weight: bold;
          }
          
          .badge-dot {
            position: absolute;
            top: -4px;
            right: -4px;
            width: 16rpx;
            height: 16rpx;
            background-color: #f56c6c;
            border-radius: 50%;
            border: 2rpx solid #fff;
          }
        }
      }
    }
  }
</style>
