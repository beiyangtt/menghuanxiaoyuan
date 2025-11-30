<template>
  <view class="container">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 0 }"
        @click="switchTab(0)">
        我的订单
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 1 }"
        @click="switchTab(1)">
        我发布的
      </view>
    </view>

    <view v-if="currentTab === 0">
      <view v-if="loading" class="loading-text">加载中...</view>
      <view v-else-if="orderList.length === 0" class="empty-state">
        <uni-icons type="list" size="80" color="#ddd"></uni-icons>
        <text class="empty-text">暂无订单</text>
      </view>
      <view v-else class="card-list">
        <view 
          class="card-item" 
          v-for="item in orderList" 
          :key="item.id">
          <view class="card-header">
            <view class="badge">{{ item.typeLabel }}</view>
            <view :class="['status', item.statusClass]">{{ item.statusText }}</view>
          </view>
          <view class="card-body" @click="openItem(item)">
            <text class="card-title">{{ item.title }}</text>
            <text class="card-price" v-if="item.price">¥{{ item.price }}</text>
            <text class="card-desc" v-if="item.description">{{ item.description }}</text>
          </view>
          <view class="card-footer">
            <text class="time-text">{{ item.timeLabel }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-else>
      <view v-if="loading" class="loading-text">加载中...</view>
      <view v-else-if="publishedList.length === 0" class="empty-state">
        <uni-icons type="compose" size="80" color="#ddd"></uni-icons>
        <text class="empty-text">暂无发布内容</text>
      </view>
      <view v-else class="card-list">
        <view 
          class="card-item" 
          v-for="item in publishedList" 
          :key="item.id">
          <view class="card-header">
            <view class="badge">{{ item.typeLabel }}</view>
            <view :class="['status', item.statusClass]">{{ item.statusText }}</view>
          </view>
          <view class="card-body" @click="openItem(item)">
            <text class="card-title">{{ item.title }}</text>
            <text class="card-desc" v-if="item.description">{{ item.description }}</text>
            <text class="card-extra" v-if="item.extra">{{ item.extra }}</text>
          </view>
          <view class="card-footer">
            <text class="time-text">{{ item.timeLabel }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import { getMyGoodsList, getMyOrderList } from '@/api/secondhand'
  import { getTaskList } from '@/api/errand'
  import { getPostList } from '@/api/forum'
  import { resolveFileUrl, formatRelativeTime, formatDateTime, toLocalDate } from '@/utils/common'
  import store from '@/store'
  
  export default {
    data() {
      return {
        currentTab: 0,
        orderList: [],
        publishedList: [],
        loading: false,
        currentUserId: null
      }
    },
    onLoad() {
      this.currentUserId = store.getters.id
      this.loadData()
    },
    onShow() {
      this.currentUserId = store.getters.id
      this.loadData()
    },
    onPullDownRefresh() {
      this.loadData().finally(() => {
        setTimeout(() => {
          uni.stopPullDownRefresh()
        }, 300)
      })
    },
    methods: {
      switchTab(index) {
        this.currentTab = index
      },
      async loadData() {
        this.loading = true
        try {
          await Promise.all([this.loadOrders(), this.loadPublished()])
        } catch (error) {
          console.error('加载数据失败:', error)
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          })
        } finally {
          this.loading = false
        }
      },
      async loadOrders() {
        const [goodsRes, taskRes] = await Promise.all([
          getMyOrderList(),
          getTaskList({ accepterId: this.currentUserId, pageNum: 1, pageSize: 200 })
        ])
        
        const goodsOrders = (goodsRes.rows || []).map(item => {
          const status = item.orderStatus || item.goodsStatus || '0'
          return {
            id: `goods-${item.orderId || item.goodsId}`,
            typeLabel: '二手订单',
            statusText: this.getOrderStatusText(status),
            statusClass: this.getOrderStatusClass(status),
            title: item.goodsTitle || '商品已删除',
            price: item.orderPrice || item.goodsPrice || '',
            description: item.orderId ? `订单号：${item.orderId}` : '',
            timeLabel: formatDateTime(item.createTime),
            timestamp: (toLocalDate(item.createTime) || { getTime: () => 0 }).getTime(),
            path: item.goodsId ? `/pages/secondhand/detail?id=${item.goodsId}` : ''
          }
        })
        
        const taskOrders = ((taskRes && (taskRes.rows || taskRes.data)) || []).map(item => {
          const descParts = []
          if (item.startLocation) descParts.push(item.startLocation)
          if (item.endLocation) descParts.push(item.endLocation)
          return {
            id: `task-${item.taskId}`,
            typeLabel: '跑腿任务',
            statusText: this.getErrandStatusText(item.taskStatus),
            statusClass: this.getErrandStatusClass(item.taskStatus),
            title: item.taskTitle || '任务',
            price: item.taskReward || item.reward || '',
            description: descParts.length ? descParts.join(' → ') : '',
            timeLabel: formatRelativeTime(item.acceptTime || item.createTime),
            timestamp: (toLocalDate(item.acceptTime || item.createTime) || { getTime: () => 0 }).getTime(),
            path: `/pages/errand/detail?id=${item.taskId}`
          }
        })
        
        this.orderList = [...goodsOrders, ...taskOrders].sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))
      },
      async loadPublished() {
        const [goodsRes, taskRes, postRes] = await Promise.all([
          getMyGoodsList(),
          getTaskList({ userId: this.currentUserId, pageNum: 1, pageSize: 200 }),
          getPostList({ userId: this.currentUserId, pageNum: 1, pageSize: 100 })
        ])
        
        const goodsList = ((goodsRes && (goodsRes.rows || goodsRes.data)) || []).map(item => {
          let image = ''
          if (item.images && item.images.trim()) {
            image = resolveFileUrl(item.images.split(',')[0].trim())
          }
          const status = item.goodsStatus || '0'
          return {
            id: `pub-goods-${item.goodsId}`,
            typeLabel: '二手',
            statusText: status === '1' ? '已售出' : '待售',
            statusClass: status === '1' ? 'status-completed' : 'status-pending',
            title: item.goodsTitle || '商品',
            description: image ? '已上传图片' : '',
            extra: item.goodsPrice ? `价格 ¥${item.goodsPrice}` : '',
            timeLabel: formatRelativeTime(item.createTime),
            timestamp: (toLocalDate(item.createTime) || { getTime: () => 0 }).getTime(),
            path: `/pages/secondhand/detail?id=${item.goodsId}`
          }
        })
        
        const taskList = ((taskRes && (taskRes.rows || taskRes.data)) || []).map(item => ({
          id: `pub-task-${item.taskId}`,
          typeLabel: '跑腿',
          statusText: this.getErrandStatusText(item.taskStatus),
          statusClass: this.getErrandStatusClass(item.taskStatus),
          title: item.taskTitle || '跑腿任务',
          description: item.taskDesc || '',
          extra: item.taskReward ? `酬劳 ¥${item.taskReward}` : '',
          timeLabel: formatRelativeTime(item.createTime),
          timestamp: (toLocalDate(item.createTime) || { getTime: () => 0 }).getTime(),
          path: `/pages/errand/detail?id=${item.taskId}`
        }))
        
        const postList = ((postRes && (postRes.rows || postRes.data)) || []).map(item => ({
          id: `pub-post-${item.postId}`,
          typeLabel: '论坛',
          statusText: `${item.commentCount || 0} 条评论`,
          statusClass: 'status-info',
          title: item.postTitle || '帖子',
          description: item.postContent ? item.postContent.slice(0, 40) + (item.postContent.length > 40 ? '...' : '') : '',
          extra: item.categoryName ? `分类：${item.categoryName}` : '',
          timeLabel: formatRelativeTime(item.createTime),
          timestamp: (toLocalDate(item.createTime) || { getTime: () => 0 }).getTime(),
          path: `/pages/forum/detail?id=${item.postId}`
        }))
        
        this.publishedList = [...goodsList, ...taskList, ...postList].sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))
      },
      openItem(item) {
        if (!item || !item.path) return
        uni.navigateTo({ url: item.path })
      },
      getOrderStatusText(status) {
        const map = {
          '0': '待支付',
          '1': '已支付',
          '2': '已完成',
          '3': '已取消'
        }
        return map[status] || '处理中'
      },
      getOrderStatusClass(status) {
        const map = {
          '0': 'status-pending',
          '1': 'status-paid',
          '2': 'status-completed',
          '3': 'status-cancelled'
        }
        return map[status] || 'status-pending'
      },
      getErrandStatusText(status) {
        const map = {
          '0': '待接单',
          '1': '进行中',
          '2': '已完成',
          '3': '已取消'
        }
        return map[status] || '未知'
      },
      getErrandStatusClass(status) {
        const map = {
          '0': 'status-pending',
          '1': 'status-paid',
          '2': 'status-completed',
          '3': 'status-cancelled'
        }
        return map[status] || 'status-pending'
      }
    }
  }
</script>

<style lang="scss" scoped>
  .container {
    min-height: 100vh;
    background-color: #f5f5f5;
  }

  .tabs {
    display: flex;
    background-color: #fff;
    border-bottom: 1rpx solid #eee;
  }

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 30rpx 0;
    font-size: 28rpx;
    color: #666;
    position: relative;
    
    &.active {
      color: #409EFF;
      font-weight: bold;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60rpx;
        height: 4rpx;
        background-color: #409EFF;
        border-radius: 2rpx;
      }
    }
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

  .card-list {
    padding: 20rpx;
  }

  .card-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
  }

  .badge {
    background-color: #ecf5ff;
    color: #409EFF;
    padding: 6rpx 20rpx;
    border-radius: 30rpx;
    font-size: 24rpx;
  }

  .status {
    font-size: 24rpx;
    padding: 6rpx 18rpx;
    border-radius: 30rpx;
    &.status-pending {
      background-color: #fff7e6;
      color: #ad6800;
    }
    &.status-paid {
      background-color: #e6fffb;
      color: #08979c;
    }
    &.status-completed {
      background-color: #f6ffed;
      color: #389e0d;
    }
    &.status-cancelled {
      background-color: #fff1f0;
      color: #cf1322;
    }
    &.status-info {
      background-color: #f0f5ff;
      color: #2f54eb;
    }
  }

  .card-body {
    padding: 10rpx 0;
  }

  .card-title {
    display: block;
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    margin-bottom: 10rpx;
  }

  .card-price {
    display: block;
    font-size: 30rpx;
    color: #f56c6c;
    font-weight: bold;
    margin-bottom: 10rpx;
  }

  .card-desc,
  .card-extra {
    display: block;
    font-size: 26rpx;
    color: #666;
    margin-bottom: 6rpx;
  }

  .card-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 10rpx;
  }

  .time-text {
    font-size: 24rpx;
    color: #999;
  }
</style>

