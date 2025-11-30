<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <uni-search-bar 
        v-model="searchKeyword" 
        placeholder="搜索帖子..." 
        @confirm="handleSearch"
        @clear="handleClearSearch"
        clearButton="auto">
      </uni-search-bar>
    </view>

    <!-- 分类标签 -->
    <view class="category-tabs">
      <scroll-view scroll-x class="tabs-scroll">
        <view 
          class="tab-item" 
          :class="{ active: currentCategory === item.categoryId }"
          v-for="item in categories" 
          :key="item.categoryId"
          @click="switchCategory(item.categoryId)">
          {{ item.categoryName }}
        </view>
      </scroll-view>
    </view>

    <!-- 帖子列表 -->
    <view class="post-list">
      <view v-if="loading" class="loading-text">加载中...</view>
      <view v-else-if="postList.length === 0" class="empty-text">暂无帖子</view>
      <view 
        v-else
        class="post-item" 
        v-for="item in postList" 
        :key="item.id"
        @click="navigateToDetail(item.id)">
        <view class="post-header">
          <image :src="item.authorAvatar" mode="aspectFill" class="author-avatar"></image>
          <view class="author-info">
            <text class="author-name">{{ item.authorName }}</text>
            <text class="post-time">{{ item.publishTime }}</text>
          </view>
          <view class="post-category">
            <text>{{ item.category }}</text>
          </view>
        </view>
        <view class="post-content">
          <text class="post-title">{{ item.title }}</text>
          <text class="post-summary" v-if="item.summary">{{ item.summary }}</text>
          <view class="post-images" v-if="item.images && item.images.length > 0">
            <image 
              v-for="(img, index) in item.images.slice(0, 3)" 
              :key="index"
              :src="img" 
              mode="aspectFill" 
              class="post-image">
            </image>
          </view>
        </view>
        <view class="post-footer">
          <view class="footer-item">
            <uni-icons type="chat" size="18" color="#999"></uni-icons>
            <text>{{ item.commentCount }}</text>
          </view>
          <view class="footer-item">
            <uni-icons type="heart" :color="item.isLiked ? '#f56c6c' : '#999'" size="18"></uni-icons>
            <text>{{ item.likeCount }}</text>
          </view>
          <view class="footer-item">
            <uni-icons type="eye" size="18" color="#999"></uni-icons>
            <text>{{ item.viewCount }}</text>
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
  import { getPostList, getCategoryList } from '@/api/forum'
  import store from '@/store'
  import { processAvatarUrl } from '@/utils/avatar'
  import { resolveFileUrl, formatRelativeTime } from '@/utils/common'
  import { updateTabBarBadge } from '@/utils/tabbar'
  
  export default {
    data() {
      return {
        currentCategory: 0,
        categories: [
          { categoryId: 0, categoryName: '全部' }
        ],
        postList: [],
        searchKeyword: '',
        loading: false,
        currentUserId: null,
        hasEntered: false
      }
    },
    onLoad() {
      this.currentUserId = store.getters.id
      this.loadCategories()
      this.loadPostList()
    },
    onShow() {
      this.currentUserId = store.getters.id
      if (this.hasEntered) {
        this.loadPostList()
      } else {
        this.hasEntered = true
      }
      updateTabBarBadge()
    },
    async onPullDownRefresh() {
      try {
        await Promise.all([this.loadCategories(), this.loadPostList()])
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
      // 加载帖子列表
      async loadPostList() {
        this.loading = true
        try {
          const params = {}
          if (this.currentCategory > 0) {
            params.categoryId = this.currentCategory
          }
          if (this.searchKeyword) {
            params.postTitle = this.searchKeyword
          }
          const res = await getPostList(params)
          if (res.code === 200) {
            this.postList = res.rows || []
            // 处理数据格式
            this.postList = this.postList.map(item => {
              let images = []
              if (item.images && item.images.trim()) {
                images = item.images.split(',').filter(url => url && url.trim())
              }
              const formattedImages = images.map(img => {
                const trimmed = img.trim()
                // 如果已经是完整URL，直接使用；否则使用 resolveFileUrl 处理
                if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
                  return trimmed
                }
                return resolveFileUrl(trimmed)
              }).filter(Boolean)
              // 处理内容摘要
              let summary = ''
              if (item.postContent) {
                summary = item.postContent.length > 50 ? item.postContent.substring(0, 50) + '...' : item.postContent
              }
              // 如果是当前用户的帖子，使用 store 中的头像（确保是最新的）
              let avatar = item.avatar
              if (item.userId === this.currentUserId && store.getters.avatar) {
                avatar = store.getters.avatar
              } else {
                // 处理头像URL，确保正确拼接baseUrl
                avatar = processAvatarUrl(avatar)
              }
              return {
                id: item.postId,
                title: item.postTitle,
                summary: summary,
                authorName: item.nickName || '用户',
                authorAvatar: avatar,
                userId: item.userId,
                publishTime: this.formatTime(item.createTime),
                category: item.categoryName || '',
                commentCount: item.commentCount || 0,
                likeCount: item.likeCount || 0,
                viewCount: item.viewCount || 0,
                isLiked: item.isLiked || false,
                images: formattedImages
              }
            })
          }
        } catch (error) {
          console.error('加载帖子列表失败:', error)
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
        this.loadPostList()
      },
      handleClearSearch() {
        this.searchKeyword = ''
        this.loadPostList()
      },
      switchCategory(id) {
        this.currentCategory = id
        this.loadPostList()
      },
      navigateToDetail(id) {
        uni.navigateTo({
          url: `/pages/forum/detail?id=${id}`
        })
      },
      navigateToPublish() {
        uni.navigateTo({
          url: '/pages/forum/publish'
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
    background-color: #fff;
    padding: 20rpx 0;
  }

  .tabs-scroll {
    white-space: nowrap;
  }

  .tab-item {
    display: inline-block;
    padding: 10rpx 30rpx;
    margin-left: 20rpx;
    border-radius: 30rpx;
    background-color: #f0f0f0;
    color: #666;
    font-size: 28rpx;
  }

  .tab-item.active {
    background-color: #E6A23C;
    color: #fff;
  }

  .post-list {
    padding: 20rpx;
  }

  .post-item {
    background-color: #fff;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
  }

  .post-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 20rpx;
  }

  .author-avatar {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    margin-right: 15rpx;
  }

  .author-info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .author-name {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 5rpx;
  }

  .post-time {
    font-size: 22rpx;
    color: #999;
  }

  .post-category {
    background-color: #fff7e6;
    color: #E6A23C;
    padding: 5rpx 15rpx;
    border-radius: 20rpx;
    font-size: 22rpx;
  }

  .post-content {
    margin-bottom: 20rpx;
  }

  .post-title {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 10rpx;
  }

  .post-summary {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
    display: block;
    margin-bottom: 15rpx;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .post-images {
    display: flex;
    flex-direction: row;
    margin-top: 15rpx;
  }

  .post-image {
    width: 200rpx;
    height: 200rpx;
    border-radius: 10rpx;
    margin-right: 10rpx;
  }

  .post-footer {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;
  }

  .footer-item {
    display: flex;
    align-items: center;
    font-size: 24rpx;
    color: #999;
  }

  .footer-item text {
    margin-left: 8rpx;
  }

  .fab-button {
    position: fixed;
    right: 40rpx;
    bottom: 120rpx;
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background-color: #E6A23C;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4rpx 12rpx rgba(230, 162, 60, 0.4);
    z-index: 999;
  }

  .loading-text, .empty-text {
    text-align: center;
    padding: 40rpx;
    color: #999;
    font-size: 28rpx;
  }
</style>

