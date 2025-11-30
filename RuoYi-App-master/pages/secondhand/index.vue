<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <uni-search-bar 
        v-model="searchKeyword" 
        placeholder="搜索商品..." 
        @confirm="handleSearch"
        @clear="handleClearSearch"
        clearButton="auto">
      </uni-search-bar>
    </view>

    <!-- 分类标签 -->
    <view class="category-tabs">
        <view 
        class="tab-item" 
        :class="{ active: currentCategory === item.categoryId }"
        v-for="item in categories" 
        :key="item.categoryId"
        @click="switchCategory(item.categoryId)">
        {{ item.categoryName }}
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="goods-list">
      <view v-if="loading" class="loading-text">加载中...</view>
      <view v-else-if="goodsList.length === 0" class="empty-text">暂无商品</view>
      <view 
        v-else
        class="goods-item" 
        v-for="item in goodsList" 
        :key="item.id"
        @click="navigateToDetail(item.id, item.status)">
        <view class="goods-image-wrapper">
          <image v-if="item.image" :src="item.image" mode="aspectFill" class="goods-image"></image>
          <view v-else class="goods-image-placeholder">
            <uni-icons type="image" size="40" color="#ddd"></uni-icons>
            <text class="placeholder-text">暂无图片</text>
          </view>
        </view>
        <view class="goods-info">
          <text class="goods-title">{{ item.title }}</text>
          <view class="goods-meta">
            <text class="goods-price">¥{{ item.price }}</text>
            <!-- 已售出标记 -->
            <view v-if="item.status === '1'" class="sold-badge">
              <text class="sold-text">已售出</text>
            </view>
            <text class="goods-time">{{ item.time }}</text>
          </view>
          <view class="goods-tags">
            <text class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 发布按钮 -->
    <view class="fab-button" @click="navigateToPublish">
      <uni-icons type="plus" size="30" color="#fff"></uni-icons>
    </view>
  </view>
</template>

<script>
  import { getGoodsList, getCategoryList } from '@/api/secondhand'
  import { resolveFileUrl, formatRelativeTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        currentCategory: 0,
        categories: [
          { categoryId: 0, categoryName: '全部' }
        ],
        goodsList: [],
        searchKeyword: '',
        loading: false,
        hasEntered: false
      }
    },
    onLoad() {
      this.loadCategories()
      this.loadGoodsList()
    },
    onShow() {
      if (this.hasEntered) {
      this.loadGoodsList()
      } else {
        this.hasEntered = true
      }
    },
    async onPullDownRefresh() {
      try {
        await Promise.all([this.loadCategories(), this.loadGoodsList()])
      } finally {
        uni.stopPullDownRefresh()
      }
    },
    methods: {
      // 加载分类列表
      async loadCategories() {
        try {
          const res = await getCategoryList()
          if (res.code === 200) {
            this.categories = [{ categoryId: 0, categoryName: '全部' }, ...res.data]
          }
        } catch (error) {
          console.error('加载分类失败:', error)
        }
      },
      // 加载商品列表
      async loadGoodsList() {
        this.loading = true
        try {
          const params = {
            // 不限制状态，显示所有商品（包括已售出的）
          }
          if (this.currentCategory > 0) {
            params.categoryId = this.currentCategory
          }
          if (this.searchKeyword) {
            params.goodsTitle = this.searchKeyword
          }
          const res = await getGoodsList(params)
          if (res.code === 200) {
            this.goodsList = res.rows || []
            // 处理数据格式
            this.goodsList = this.goodsList.map(item => {
              let imageUrl = null
              if (item.images && item.images.trim()) {
                const images = item.images.split(',').filter(url => url && url.trim())
                if (images.length > 0) {
                  const imgPath = images[0].trim()
                  // 如果已经是完整URL，直接使用；否则使用 resolveFileUrl 处理
                  if (imgPath.startsWith('http://') || imgPath.startsWith('https://')) {
                    imageUrl = imgPath
                  } else {
                    imageUrl = resolveFileUrl(imgPath)
                  }
                }
              }
              return {
                id: item.goodsId,
                title: item.goodsTitle,
                price: item.goodsPrice,
                image: imageUrl,
                status: item.goodsStatus || '0',
                time: this.formatTime(item.createTime),
                tags: item.goodsStatus === '0' ? ['待售'] : []
              }
            })
          }
        } catch (error) {
          console.error('加载商品列表失败:', error)
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          })
        } finally {
          this.loading = false
        }
      },
      handleSearch(e) {
        this.searchKeyword = e.value || ''
        this.loadGoodsList()
      },
      handleClearSearch() {
        this.searchKeyword = ''
        this.loadGoodsList()
      },
      switchCategory(id) {
        this.currentCategory = id
        this.loadGoodsList()
      },
      navigateToDetail(id, status) {
        // 如果商品已售出，不允许其他用户访问详情
        if (status === '1') {
          // 这里需要判断是否是发布者，但由于列表中没有userId，我们在详情页判断
          // 先允许跳转，在详情页进行拦截
        }
        uni.navigateTo({
          url: `/pages/secondhand/detail?id=${id}`
        })
      },
      navigateToPublish() {
        uni.navigateTo({
          url: '/pages/secondhand/publish'
        })
      },
      formatTime(time) {
        return formatRelativeTime(time)
      }
    }
  }
</script>

<style scoped>
  .container {
    background-color: #f5f5f5;
    min-height: 100vh;
    padding-bottom: 100rpx;
  }

  .search-bar {
    background-color: #fff;
    padding: 20rpx;
  }

  .category-tabs {
    display: flex;
    flex-direction: row;
    background-color: #fff;
    padding: 20rpx;
    overflow-x: auto;
    white-space: nowrap;
  }

  .tab-item {
    padding: 10rpx 30rpx;
    margin-right: 20rpx;
    border-radius: 30rpx;
    background-color: #f0f0f0;
    color: #666;
    font-size: 28rpx;
  }

  .tab-item.active {
    background-color: #409EFF;
    color: #fff;
  }

  .goods-list {
    padding: 20rpx;
  }

  .goods-item {
    background-color: #fff;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
    display: flex;
    flex-direction: row;
    padding: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
    position: relative;
  }

  .goods-image-wrapper {
    position: relative;
    width: 200rpx;
    height: 200rpx;
    margin-right: 20rpx;
  }

  .goods-image {
    width: 200rpx;
    height: 200rpx;
    border-radius: 10rpx;
  }

  .goods-image-placeholder {
    width: 200rpx;
    height: 200rpx;
    border-radius: 10rpx;
    background-color: #f5f5f5;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border: 1rpx dashed #ddd;
  }

  .placeholder-text {
    font-size: 22rpx;
    color: #999;
    margin-top: 10rpx;
  }

  .sold-badge {
    width: 140rpx;
    height: 140rpx;
    background-color: #e5e5e5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 20rpx;
    flex-shrink: 0;
  }

  .sold-text {
    color: #f56c6c;
    font-size: 28rpx;
    font-weight: bold;
    transform: rotate(-8deg);
    letter-spacing: 2rpx;
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
    justify-content: space-between;
    align-items: center;
    margin-top: 20rpx;
    flex-wrap: wrap;
  }

  .goods-price {
    font-size: 36rpx;
    color: #f56c6c;
    font-weight: bold;
  }

  .goods-time {
    font-size: 24rpx;
    color: #999;
  }

  .goods-tags {
    display: flex;
    flex-direction: row;
    margin-top: 10rpx;
  }

  .tag {
    font-size: 20rpx;
    color: #409EFF;
    background-color: #ecf5ff;
    padding: 4rpx 10rpx;
    border-radius: 4rpx;
    margin-right: 10rpx;
  }

  .fab-button {
    position: fixed;
    right: 40rpx;
    bottom: 120rpx;
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background-color: #409EFF;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4rpx 12rpx rgba(64, 158, 255, 0.4);
    z-index: 999;
  }

  .loading-text, .empty-text {
    text-align: center;
    padding: 100rpx 0;
    color: #999;
    font-size: 28rpx;
  }
</style>

