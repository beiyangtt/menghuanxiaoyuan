<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <uni-search-bar 
        v-model="searchKeyword" 
        placeholder="搜索跑腿服务..." 
        @confirm="handleSearch"
        @clear="handleClearSearch"
        clearButton="auto">
      </uni-search-bar>
    </view>

    <!-- 服务类型 -->
    <view class="service-types">
      <view 
        class="type-item" 
        :class="{ active: currentType === item.id }"
        v-for="item in serviceTypes" 
        :key="item.id"
        @click="switchType(item.id)">
        <uni-icons :type="item.icon" size="30" :color="currentType === item.id ? '#409EFF' : '#666'"></uni-icons>
        <text class="type-text">{{ item.name }}</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <view class="task-list">
      <view v-if="loading" class="loading-text">加载中...</view>
      <view v-else-if="taskList.length === 0" class="empty-text">暂无任务</view>
      <view 
        v-else
        class="task-item" 
        v-for="item in taskList" 
        :key="item.id"
        @click="navigateToDetail(item.id, item.status)">
        <view class="task-header">
          <text class="task-title">{{ item.title }}</text>
          <view class="task-reward-wrapper">
            <text class="task-reward">¥{{ item.reward }}</text>
            <!-- 已完成标记 -->
            <view v-if="item.status === '2'" class="completed-badge">
              <text class="completed-text">已完成</text>
            </view>
          </view>
        </view>
        <view class="task-content">
          <view class="task-info">
            <text class="info-label">起点：</text>
            <text class="info-value">{{ item.start }}</text>
          </view>
          <view class="task-info">
            <text class="info-label">终点：</text>
            <text class="info-value">{{ item.end }}</text>
          </view>
          <view class="task-info">
            <text class="info-label">时间：</text>
            <text class="info-value">{{ item.time }}</text>
          </view>
        </view>
        <view class="task-footer">
          <text class="task-time">{{ item.publishTime }}</text>
          <text class="task-status" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</text>
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
  import { getTaskList } from '@/api/errand'
  import { formatRelativeTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        currentType: 0,
        searchKeyword: '',
        loading: false,
        serviceTypes: [
          { id: 0, name: '全部', icon: 'grid' },
          { id: 1, name: '代取', icon: 'paperplane' },
          { id: 2, name: '代买', icon: 'cart' },
          { id: 3, name: '代送', icon: 'location' },
          { id: 4, name: '其他', icon: 'more' }
        ],
        taskList: [],
        hasEntered: false
      }
    },
    onLoad() {
      this.loadTaskList()
    },
    onShow() {
      if (this.hasEntered) {
        this.loadTaskList()
      } else {
        this.hasEntered = true
      }
    },
    async onPullDownRefresh() {
      try {
        await this.loadTaskList()
      } finally {
        uni.stopPullDownRefresh()
      }
    },
    methods: {
      // 加载任务列表
      async loadTaskList() {
        this.loading = true
        try {
          const params = {
            // 不限制状态，显示所有任务（包括已完成的）
          }
          if (this.currentType > 0) {
            // 根据类型筛选，需要后端支持
            // params.categoryId = this.currentType
          }
          if (this.searchKeyword) {
            params.taskTitle = this.searchKeyword
          }
          const res = await getTaskList(params)
          if (res.code === 200) {
            this.taskList = (res.rows || []).map(item => {
              return {
                id: item.taskId,
                title: item.taskTitle,
                reward: item.taskReward || item.reward || 0,
                start: item.startLocation || '',
                end: item.endLocation || '',
                time: item.deadline ? this.formatTime(item.deadline) : (item.timeRequirement || ''),
                publishTime: this.formatTime(item.createTime),
                status: item.taskStatus || '0'
              }
            })
          } else {
            uni.showToast({
              title: res.msg || '加载失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('加载任务列表失败:', error)
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
        this.loadTaskList()
      },
      handleClearSearch() {
        this.searchKeyword = ''
        this.loadTaskList()
      },
      switchType(id) {
        this.currentType = id
        this.loadTaskList()
      },
      getStatusText(status) {
        const statusMap = {
          '0': '待接单',
          '1': '进行中',
          '2': '已完成',
          '3': '已取消'
        }
        return statusMap[status] || '未知'
      },
      getStatusClass(status) {
        const classMap = {
          '0': 'pending',
          '1': 'accepted',
          '2': 'completed',
          '3': 'cancelled'
        }
        return classMap[status] || ''
      },
      navigateToDetail(id, status) {
        // 如果任务已完成，不允许其他用户访问详情
        if (status === '2') {
          // 在详情页进行拦截
        }
        uni.navigateTo({
          url: `/pages/errand/detail?id=${id}`
        })
      },
      navigateToPublish() {
        uni.navigateTo({
          url: '/pages/errand/publish'
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

  .service-types {
    display: flex;
    flex-direction: row;
    background-color: #fff;
    padding: 30rpx 20rpx;
    justify-content: space-around;
  }

  .type-item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .type-item.active .type-text {
    color: #409EFF;
  }

  .type-text {
    font-size: 24rpx;
    color: #666;
    margin-top: 10rpx;
  }

  .task-list {
    padding: 20rpx;
  }

  .task-item {
    background-color: #fff;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
  }

  .loading-text, .empty-text {
    text-align: center;
    padding: 100rpx 0;
    color: #999;
    font-size: 28rpx;
  }

  .task-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    flex-wrap: wrap;
  }

  .task-title {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    flex: 1;
    margin-right: 20rpx;
  }

  .task-reward-wrapper {
    display: flex;
    align-items: center;
    flex-shrink: 0;
  }

  .task-reward {
    font-size: 36rpx;
    color: #f56c6c;
    font-weight: bold;
    margin-right: 20rpx;
  }

  .completed-badge {
    width: 140rpx;
    height: 140rpx;
    background-color: #e5e5e5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .completed-text {
    color: #f56c6c;
    font-size: 28rpx;
    font-weight: bold;
    transform: rotate(-8deg);
    letter-spacing: 2rpx;
  }

  .task-content {
    margin-bottom: 20rpx;
  }

  .task-info {
    display: flex;
    flex-direction: row;
    margin-bottom: 10rpx;
    font-size: 28rpx;
  }

  .info-label {
    color: #999;
    width: 100rpx;
  }

  .info-value {
    color: #333;
    flex: 1;
  }

  .task-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;
  }

  .task-time {
    font-size: 24rpx;
    color: #999;
  }

  .task-status {
    font-size: 24rpx;
    padding: 5rpx 15rpx;
    border-radius: 20rpx;
    background-color: #f0f0f0;
    color: #666;
  }

  .task-status.pending {
    background-color: #ecf5ff;
    color: #409EFF;
  }

  .task-status.accepted {
    background-color: #f0f9ff;
    color: #67C23A;
  }

  .task-status.completed {
    background-color: #f0f0f0;
    color: #999;
  }

  .task-status.cancelled {
    background-color: #f0f0f0;
    color: #999;
  }

  .fab-button {
    position: fixed;
    right: 40rpx;
    bottom: 120rpx;
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background-color: #67C23A;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4rpx 12rpx rgba(103, 194, 58, 0.4);
    z-index: 999;
  }
</style>

