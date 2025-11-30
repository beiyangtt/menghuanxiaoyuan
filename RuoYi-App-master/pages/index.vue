<template>
  <view class="content">
    <!-- 顶部轮播图 -->
    <swiper class="swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500" circular>
      <swiper-item v-for="(item, index) in banners" :key="index">
        <image :src="item.image" mode="aspectFill" class="swiper-image"></image>
      </swiper-item>
    </swiper>

    <!-- 功能入口 -->
    <view class="function-grid">
      <view class="grid-item" @click="navigateTo('/pages/secondhand/index')">
        <view class="icon-wrapper" style="background-color: #ecf5ff;">
          <uni-icons type="shop" size="40" color="#409EFF"></uni-icons>
        </view>
        <text class="grid-text">二手市场</text>
      </view>
      <view class="grid-item" @click="navigateTo('/pages/errand/index')">
        <view class="icon-wrapper" style="background-color: #f0f9ff;">
          <uni-icons type="paperplane" size="40" color="#67C23A"></uni-icons>
        </view>
        <text class="grid-text">跑腿服务</text>
      </view>
      <view class="grid-item" @click="handleMoreFunction">
        <view class="icon-wrapper" style="background-color: #f5f7fa;">
          <uni-icons type="more-filled" size="40" color="#909399"></uni-icons>
        </view>
        <text class="grid-text">更多功能</text>
      </view>
    </view>

    <!-- 热门推荐 -->
    <view class="section">
      <view class="section-title">
        <text class="title-text">热门推荐</text>
      </view>
      
      <!-- 二手推荐 -->
      <view class="recommend-category">
        <view class="category-header">
          <uni-icons type="shop" size="20" color="#409EFF"></uni-icons>
          <text class="category-title">热门二手</text>
          <text class="more-text" @click="navigateTo('/pages/secondhand/index')">更多 ></text>
        </view>
        <view class="recommend-list">
          <view class="recommend-item" v-for="(item, index) in hotSecondhand" :key="item.id || index" @click="navigateTo('/pages/secondhand/detail?id=' + item.id)">
            <image v-if="item.image" :src="item.image" mode="aspectFill" class="recommend-image"></image>
            <view v-else class="recommend-image-placeholder">
              <uni-icons type="image" size="40" color="#ddd"></uni-icons>
            </view>
            <view class="recommend-info">
              <text class="recommend-title">{{ item.title }}</text>
              <text class="recommend-price">¥{{ item.price }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 跑腿推荐 -->
      <view class="recommend-category">
        <view class="category-header">
          <uni-icons type="paperplane" size="20" color="#67C23A"></uni-icons>
          <text class="category-title">热门跑腿</text>
          <text class="more-text" @click="navigateTo('/pages/errand/index')">更多 ></text>
        </view>
        <view class="recommend-list">
          <view class="recommend-item" v-for="(item, index) in hotErrand" :key="item.id || index" @click="navigateTo('/pages/errand/detail?id=' + item.id)">
            <view class="errand-info">
              <text class="errand-title">{{ item.title }}</text>
              <view class="errand-location">
                <text class="location-text">{{ item.start }} → {{ item.end }}</text>
              </view>
              <view class="errand-footer">
                <text class="errand-reward">¥{{ item.reward }}</text>
                <text class="errand-time">{{ item.time }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 校园公告 -->
    <view class="section">
      <view class="section-title">
        <uni-icons type="sound" size="20" color="#E6A23C"></uni-icons>
        <text class="title-text">校园公告</text>
      </view>
      
      <view class="notice-list">
        <view 
          class="notice-item" 
          v-for="(item, index) in notices" 
          :key="index"
          @click="handleNoticeClick(item)">
          <view class="notice-content">
            <view class="notice-header">
              <text class="notice-title">{{ item.title }}</text>
              <view v-if="item.isNew" class="notice-badge">新</view>
            </view>
            <text class="notice-summary">{{ item.summary }}</text>
            <view class="notice-footer">
              <text class="notice-time">{{ item.time }}</text>
              <text class="notice-department">{{ item.department }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import { getHotGoods } from '@/api/secondhand'
  import { getHotTasks } from '@/api/errand'
  import { resolveFileUrl } from '@/utils/common'
  import { updateTabBarBadge } from '@/utils/tabbar'
  
  export default {
    data() {
      return {
        banners: [{
            image: '/static/images/banner/banner01.jpg'
          },
          {
            image: '/static/images/banner/banner02.jpg'
          },
          {
            image: '/static/images/banner/banner03.jpg'
          }
        ],
        hotSecondhand: [],
        hotErrand: [],
        notices: [
          {
            id: 1,
            title: '关于2027年春季学期开学通知',
            summary: '根据学校安排，2027年春季学期将于3月1日正式开学，请同学们提前做好返校准备...',
            time: '2027-02-15',
            department: '教务处',
            isNew: true
          },
          {
            id: 2,
            title: '图书馆开放时间调整通知',
            summary: '为更好地服务师生，图书馆自即日起调整开放时间，周一至周日：8:00-22:00...',
            time: '2026-02-10',
            department: '图书馆',
            isNew: true
          },
          {
            id: 3,
            title: '校园网络维护通知',
            summary: '为提升网络服务质量，学校将于本周六（2月17日）凌晨2:00-6:00进行网络维护...',
            time: '2026-02-08',
            department: '信息中心',
            isNew: false
          },
          {
            id: 4,
            title: '学生宿舍安全检查通知',
            summary: '为保障学生住宿安全，学校将于下周开展宿舍安全大检查，请同学们配合检查工作...',
            time: '2026-02-05',
            department: '学生处',
            isNew: false
          }
        ]
      }
    },
    onLoad() {
      this.loadHotRecommendations()
    },
    onShow() {
      // 更新底部导航栏消息徽章
      updateTabBarBadge()
    },
    onPullDownRefresh() {
      // 刷新热门推荐数据
      this.loadHotRecommendations()
      setTimeout(() => {
        uni.stopPullDownRefresh()
      }, 1000)
    },
    methods: {
      // 加载热门推荐数据
      async loadHotRecommendations() {
        try {
          // 加载热门二手商品
          const goodsRes = await getHotGoods(10)
          if (goodsRes.code === 200) {
            this.hotSecondhand = goodsRes.data.map(item => {
              let imageUrl = null
              if (item.images && item.images.trim()) {
                const images = item.images.split(',').filter(url => url && url.trim())
                if (images.length > 0) {
                  imageUrl = resolveFileUrl(images[0].trim())
                }
              }
              return {
                id: item.goodsId,
                title: item.goodsTitle,
                price: item.goodsPrice,
                image: imageUrl
              }
            })
          }
          
          // 加载热门跑腿任务
          const taskRes = await getHotTasks(10)
          if (taskRes.code === 200) {
            this.hotErrand = taskRes.data.map(item => ({
              id: item.taskId,
              title: item.taskTitle,
              reward: item.taskReward || item.reward,
              start: item.startLocation,
              end: item.endLocation,
              time: item.timeRequirement || item.deadline || ''
            }))
          }
        } catch (error) {
          console.error('加载热门推荐失败:', error)
        }
      },
      navigateTo(url) {
        uni.navigateTo({
          url: url
        })
      },
      handleMoreFunction() {
        uni.showToast({
          title: '功能开发中，敬请期待',
          icon: 'none',
          duration: 2000
        })
      },
      handleNoticeClick(item) {
        uni.showModal({
          title: item.title,
          content: item.summary + '\n\n发布部门：' + item.department + '\n发布时间：' + item.time,
          showCancel: false,
          confirmText: '知道了'
        })
      }
    }
  }
</script>

<style scoped>
  .content {
    background-color: #f5f5f5;
    min-height: 100vh;
  }

  .swiper {
    width: 100%;
    height: 300rpx;
  }

  .swiper-image {
    width: 100%;
    height: 100%;
  }

  .function-grid {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    padding: 40rpx 20rpx;
    background-color: #fff;
    margin: 20rpx;
    border-radius: 20rpx;
  }

  .grid-item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .icon-wrapper {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10rpx;
  }

  .grid-text {
    font-size: 24rpx;
    color: #333;
  }

  .badge {
    position: absolute;
    top: -10rpx;
    right: -10rpx;
    background-color: #f56c6c;
    color: #fff;
    font-size: 20rpx;
    min-width: 32rpx;
    height: 32rpx;
    line-height: 32rpx;
    text-align: center;
    border-radius: 16rpx;
    padding: 0 8rpx;
    box-sizing: border-box;
    z-index: 10;
    font-weight: bold;
  }

  .section {
    margin: 20rpx;
    background-color: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
  }

  .section-title {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 20rpx;
  }

  .section-title .title-text {
    margin-left: 10rpx;
  }

  .title-text {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }

  .more-text {
    font-size: 24rpx;
    color: #999;
  }

  .recommend-category {
    margin-bottom: 40rpx;
  }

  .recommend-category:last-child {
    margin-bottom: 0;
  }

  .category-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 20rpx;
  }

  .category-title {
    font-size: 28rpx;
    color: #333;
    font-weight: bold;
    margin-left: 10rpx;
    flex: 1;
  }

  .recommend-list {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
  }

  .recommend-item {
    width: 48%;
    margin-bottom: 20rpx;
    background-color: #f9f9f9;
    border-radius: 10rpx;
    overflow: hidden;
  }

  .recommend-image {
    width: 100%;
    height: 200rpx;
  }

  .recommend-image-placeholder {
    width: 100%;
    height: 200rpx;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .recommend-info {
    padding: 20rpx;
    display: flex;
    flex-direction: column;
  }

  .recommend-title {
    font-size: 28rpx;
    color: #333;
    display: block;
    margin-bottom: 10rpx;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
  }

  .recommend-price {
    font-size: 32rpx;
    color: #f56c6c;
    font-weight: bold;
  }

  .errand-info {
    padding: 20rpx;
    display: flex;
    flex-direction: column;
  }

  .errand-title {
    font-size: 28rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 10rpx;
  }

  .errand-location {
    margin-bottom: 10rpx;
  }

  .location-text {
    font-size: 24rpx;
    color: #666;
  }

  .errand-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .errand-reward {
    font-size: 32rpx;
    color: #67C23A;
    font-weight: bold;
  }

  .errand-time {
    font-size: 22rpx;
    color: #999;
  }

  .forum-recommend {
    width: 100%;
    padding: 20rpx;
    background-color: #fff;
    border: 1rpx solid #f0f0f0;
    border-radius: 10rpx;
  }

  .forum-recommend-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 15rpx;
  }

  .forum-recommend-avatar {
    width: 50rpx;
    height: 50rpx;
    border-radius: 50%;
    margin-right: 15rpx;
  }

  .forum-recommend-info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .forum-recommend-author {
    font-size: 26rpx;
    color: #333;
    margin-bottom: 5rpx;
  }

  .forum-recommend-time {
    font-size: 22rpx;
    color: #999;
  }

  .forum-recommend-category {
    background-color: #fff7e6;
    color: #E6A23C;
    padding: 4rpx 12rpx;
    border-radius: 20rpx;
    font-size: 20rpx;
  }

  .forum-recommend-title {
    font-size: 30rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 15rpx;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .forum-recommend-stats {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .stat-item {
    font-size: 22rpx;
    color: #999;
    margin-right: 20rpx;
  }

  .notice-list {
    display: flex;
    flex-direction: column;
  }

  .notice-item {
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .notice-item:last-child {
    border-bottom: none;
  }

  .notice-content {
    display: flex;
    flex-direction: column;
  }

  .notice-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 10rpx;
  }

  .notice-title {
    font-size: 30rpx;
    color: #333;
    font-weight: bold;
    flex: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
  }

  .notice-badge {
    background-color: #f56c6c;
    color: #fff;
    font-size: 20rpx;
    padding: 2rpx 10rpx;
    border-radius: 10rpx;
    margin-left: 10rpx;
  }

  .notice-summary {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
    margin-bottom: 15rpx;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .notice-footer {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .notice-time {
    font-size: 22rpx;
    color: #999;
  }

  .notice-department {
    font-size: 22rpx;
    color: #E6A23C;
    background-color: #fff7e6;
    padding: 4rpx 12rpx;
    border-radius: 10rpx;
  }
</style>
