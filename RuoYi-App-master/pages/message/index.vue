<template>
  <view class="container">
    <!-- 消息分类标签 -->
    <view class="message-tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === item.id }"
        v-for="item in tabs" 
        :key="item.id"
        @click="switchTab(item.id)">
        <text>{{ item.name }}</text>
        <text class="badge" v-if="item.badge > 0">{{ item.badge }}</text>
      </view>
    </view>

    <!-- 消息列表 -->
    <view class="message-list" v-if="messageList.length > 0">
      <uni-swipe-action>
        <uni-swipe-action-item
          v-for="item in messageList"
          :key="item.id"
          :right-options="swipeOptions"
          @click="handleSwipeAction($event, item)"
        >
          <view 
            class="message-item"
            @click.stop="navigateToChat(item)"
            @longpress="confirmDelete(item)"
          >
            <image :src="item.avatar" mode="aspectFill" class="message-avatar"></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-name">{{ item.name }}</text>
                <text class="message-time">{{ item.time }}</text>
              </view>
              <text class="message-text">{{ item.lastMessage }}</text>
            </view>
            <view class="message-badge" v-if="item.unreadCount > 0">
              <text>{{ item.unreadCount > 99 ? '99+' : item.unreadCount }}</text>
            </view>
          </view>
        </uni-swipe-action-item>
      </uni-swipe-action>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-else>
      <uni-icons type="chatbubbles" size="80" color="#ddd"></uni-icons>
      <text class="empty-text">暂无消息</text>
    </view>
  </view>
</template>

<script>
  import { 
    getChatSessionList, 
    getMessageList, 
    getUnreadCount,
    deleteSystemMessage,
    deleteChatSession
  } from '@/api/message'
  import { processAvatarUrl } from '@/utils/avatar'
  import store from '@/store'
  import { updateTabBarBadge } from '@/utils/tabbar'
  import { formatRelativeTime, toLocalDate } from '@/utils/common'
  
  export default {
    data() {
      return {
        currentTab: 0,
        tabs: [
          { id: 0, name: '全部', badge: 0, type: null },
          { id: 1, name: '评论回复', badge: 0, type: 'comment' }
        ],
        chatSessionList: [],
        systemMessageList: [],
        loading: false,
        currentUserId: null,
        swipeOptions: [
          {
            text: '删除',
            style: {
              backgroundColor: '#f56c6c',
              color: '#fff',
              fontSize: '28rpx'
            }
          }
        ]
      }
    },
    computed: {
      messageList() {
        let list = []
        if (this.currentTab === 0) {
          list = [...this.chatSessionList, ...this.systemMessageList]
        } else {
          list = [...this.systemMessageList]
        }
        return list.sort((a, b) => {
          const ta = a.timestamp || 0
          const tb = b.timestamp || 0
          return tb - ta
        })
      }
    },
    onLoad() {
      this.currentUserId = store.getters.id
      this.refreshAll()
    },
    onShow() {
      this.currentUserId = store.getters.id
      this.refreshAll()
      updateTabBarBadge()
    },
    async onPullDownRefresh() {
      try {
        await this.refreshAll()
      } finally {
        uni.stopPullDownRefresh()
      }
    },
    methods: {
      async refreshAll() {
        await Promise.all([
          this.loadChatSessions(),
          this.loadSystemMessages(),
          this.loadUnreadCounts()
        ])
      },
      // 加载聊天会话列表
      async loadChatSessions() {
        try {
          const res = await getChatSessionList()
          if (res.code === 200) {
            this.chatSessionList = (res.data || []).map(item => ({
              id: item.sessionId,
              sessionId: item.sessionId,
              name: item.otherNickName || '用户',
              avatar: processAvatarUrl(item.otherAvatar),
              lastMessage: item.lastMessage || '',
              time: formatRelativeTime(item.lastMessageTime || item.updateTime),
              unreadCount: item.user1Id === this.currentUserId ? item.user1Unread : item.user2Unread,
              type: 'chat',
              otherUserId: item.otherUserId,
              timestamp: (toLocalDate(item.lastMessageTime || item.updateTime) || { getTime: () => 0 }).getTime()
            }))
          }
        } catch (error) {
          console.error('加载聊天会话失败:', error)
        }
      },
      // 加载系统消息列表
      async loadSystemMessages() {
        try {
          const res = await getMessageList('comment')
          if (res.code === 200) {
            this.systemMessageList = (res.data || []).map(item => ({
              id: item.messageId,
              messageId: item.messageId,
              name: item.senderNickName || (item.senderId === 0 ? '系统通知' : '用户'),
              avatar: processAvatarUrl(item.senderAvatar),
              lastMessage: item.messageContent || '',
              time: formatRelativeTime(item.createTime),
              unreadCount: item.isRead === '0' ? 1 : 0,
              type: item.messageType || 'system',
              relatedId: item.relatedId,
              relatedType: item.relatedType,
              timestamp: (toLocalDate(item.createTime) || { getTime: () => 0 }).getTime()
            }))
          }
        } catch (error) {
          console.error('加载系统消息失败:', error)
        }
      },
      // 加载未读数
      async loadUnreadCounts() {
        try {
          for (let tab of this.tabs) {
            if (tab.type) {
              const res = await getUnreadCount(tab.type)
              if (res.code === 200) {
                tab.badge = res.data || 0
              }
            }
          }
        } catch (error) {
          console.error('加载未读数失败:', error)
        }
      },
      switchTab(id) {
        this.currentTab = id
        if (id === 0) {
          this.loadChatSessions()
        }
        this.loadSystemMessages()
      },
      navigateToChat(item) {
        if (item.type === 'chat') {
          // 跳转到聊天界面
          uni.navigateTo({
            url: `/pages/message/chat?sessionId=${item.sessionId}&userId=${item.otherUserId}`
          })
        } else {
          // 系统消息，根据消息类型跳转到相关详情页
          if (item.type === 'comment' && item.relatedId) {
            // 评论回复消息，跳转到帖子详情页
            uni.navigateTo({
              url: `/pages/forum/detail?id=${item.relatedId}`
            })
            // 标记消息为已读
            if (item.messageId) {
              this.markMessageAsRead(item.messageId)
            }
          } else {
            // 其他系统消息
            uni.showToast({
              title: '系统消息',
              icon: 'none'
            })
          }
        }
      },
      // 标记消息为已读
      async markMessageAsRead(messageId) {
        try {
          const { markMessageAsRead: markRead } = await import('@/api/message')
          await markRead(messageId)
          // 重新加载消息列表
          this.loadSystemMessages()
          this.loadUnreadCounts()
        } catch (error) {
          console.error('标记消息已读失败:', error)
        }
      },
      handleSwipeAction(e, item) {
        if (e && e.detail && e.detail.index === 0) {
          this.confirmDelete(item)
        }
      },
      confirmDelete(item) {
        uni.showModal({
          title: '提示',
          content: '确定删除该消息吗？',
          success: (res) => {
            if (res.confirm) {
              this.deleteMessageItem(item)
            }
          }
        })
      },
      async deleteMessageItem(item) {
        try {
          if (item.type === 'chat') {
            const res = await deleteChatSession(item.sessionId)
            if (res.code === 200) {
              this.chatSessionList = this.chatSessionList.filter(msg => msg.id !== item.id)
              uni.showToast({ title: '已删除会话', icon: 'success' })
            } else {
              uni.showToast({ title: res.msg || '删除失败', icon: 'none' })
              return
            }
          } else {
            const res = await deleteSystemMessage(item.messageId)
            if (res.code === 200) {
              this.systemMessageList = this.systemMessageList.filter(msg => msg.id !== item.id)
              uni.showToast({ title: '已删除消息', icon: 'success' })
            } else {
              uni.showToast({ title: res.msg || '删除失败', icon: 'none' })
              return
            }
          }
          this.loadUnreadCounts()
          updateTabBarBadge()
        } catch (error) {
          console.error('删除消息失败:', error)
          uni.showToast({ title: '删除失败，请重试', icon: 'none' })
        }
      }
    }
  }
</script>

<style scoped>
  .container {
    background-color: #f5f5f5;
    min-height: 100vh;
  }

  .message-tabs {
    display: flex;
    flex-direction: row;
    background-color: #fff;
    padding: 20rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 15rpx 0;
    position: relative;
    font-size: 28rpx;
    color: #666;
  }

  .tab-item.active {
    color: #409EFF;
    font-weight: bold;
  }

  .tab-item.active::after {
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

  .badge {
    position: absolute;
    top: 5rpx;
    right: 10rpx;
    background-color: #f56c6c;
    color: #fff;
    font-size: 20rpx;
    padding: 2rpx 8rpx;
    border-radius: 20rpx;
    min-width: 30rpx;
    text-align: center;
  }

  .message-list {
    padding: 20rpx;
  }

  .message-item {
    background-color: #fff;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
    padding: 25rpx;
    display: flex;
    flex-direction: row;
    align-items: center;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
  }

  .message-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    margin-right: 20rpx;
  }

  .message-content {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .message-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10rpx;
  }

  .message-name {
    font-size: 30rpx;
    color: #333;
    font-weight: bold;
  }

  .message-time {
    font-size: 22rpx;
    color: #999;
  }

  .message-text {
    font-size: 26rpx;
    color: #666;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
  }

  .message-badge {
    width: 40rpx;
    height: 40rpx;
    background-color: #f56c6c;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 15rpx;
  }

  .message-badge text {
    color: #fff;
    font-size: 20rpx;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 200rpx 0;
  }

  .empty-text {
    font-size: 28rpx;
    color: #999;
    margin-top: 30rpx;
  }
</style>

