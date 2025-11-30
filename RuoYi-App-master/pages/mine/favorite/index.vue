<template>
  <view class="container">
    <view v-if="loading" class="loading-text">加载中...</view>
    <view v-else-if="favoriteList.length === 0" class="empty-state">
      <uni-icons type="star" size="80" color="#ddd"></uni-icons>
      <text class="empty-text">暂无收藏</text>
    </view>
    <view v-else class="goods-list">
      <view 
        class="goods-item" 
        v-for="item in favoriteList" 
        :key="item.goodsId"
        @click="navigateToDetail(item.goodsId)">
        <view class="goods-image-wrapper">
          <image v-if="item.image" :src="item.image" mode="aspectFill" class="goods-image"></image>
          <view v-else class="goods-image-placeholder">
            <uni-icons type="image" size="40" color="#ddd"></uni-icons>
          </view>
        </view>
        <view class="goods-info">
          <text class="goods-title">{{ item.title }}</text>
          <view class="goods-meta">
            <text class="goods-price">¥{{ item.price }}</text>
            <view v-if="item.status === '1'" class="sold-badge">
              <text class="sold-text">已售出</text>
            </view>
          </view>
          <text class="goods-time">{{ item.time }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import { getFavoriteList } from '@/api/secondhand'
  import { resolveFileUrl, formatRelativeTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        favoriteList: [],
        loading: false
      }
    },
    onLoad() {
      this.loadFavoriteList()
    },
    onPullDownRefresh() {
      this.loadFavoriteList()
      setTimeout(() => {
        uni.stopPullDownRefresh()
      }, 1000)
    },
    methods: {
      async loadFavoriteList() {
        this.loading = true
        try {
          const res = await getFavoriteList()
          if (res.code === 200) {
            this.favoriteList = (res.data || []).map(item => {
              let image = ''
              if (item.images && item.images.trim()) {
                image = resolveFileUrl(item.images.split(',')[0].trim())
              }
              return {
                goodsId: item.goodsId,
                title: item.goodsTitle,
                price: item.goodsPrice,
                image,
                status: item.goodsStatus,
                time: this.formatTime(item.createTime)
              }
            })
          }
        } catch (error) {
          console.error('加载收藏列表失败:', error)
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          })
        } finally {
          this.loading = false
        }
      },
      navigateToDetail(goodsId) {
        uni.navigateTo({
          url: `/pages/secondhand/detail?id=${goodsId}`
        })
      },
      formatTime(time) {
        return formatRelativeTime(time)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding: 20rpx;
  }

  .loading-text {
    text-align: center;
    padding: 40rpx;
    color: #999;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;
    
    .empty-text {
      margin-top: 20rpx;
      color: #999;
      font-size: 28rpx;
    }
  }

  .goods-list {
    .goods-item {
      display: flex;
      background-color: #fff;
      border-radius: 10rpx;
      margin-bottom: 20rpx;
      padding: 20rpx;
      box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    }

    .goods-image-wrapper {
      width: 200rpx;
      height: 200rpx;
      margin-right: 20rpx;
      border-radius: 10rpx;
      overflow: hidden;
      flex-shrink: 0;
    }

    .goods-image {
      width: 100%;
      height: 100%;
    }

    .goods-image-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f5f5;
    }

    .goods-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    .goods-title {
      font-size: 30rpx;
      color: #333;
      margin-bottom: 10rpx;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }

    .goods-meta {
      display: flex;
      align-items: center;
      margin-bottom: 10rpx;
    }

    .goods-price {
      font-size: 36rpx;
      color: #f56c6c;
      font-weight: bold;
      margin-right: 20rpx;
    }

    .sold-badge {
      background-color: #f0f0f0;
      padding: 4rpx 12rpx;
      border-radius: 4rpx;
    }

    .sold-text {
      font-size: 22rpx;
      color: #999;
    }

    .goods-time {
      font-size: 24rpx;
      color: #999;
    }
  }
</style>

