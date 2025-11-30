<template>
  <view class="container">
    <!-- 聊天消息列表 -->
    <scroll-view 
      class="message-list" 
      scroll-y 
      :scroll-top="scrollTop"
      scroll-with-animation
      @scrolltolower="loadMore">
      <!-- 加载状态 -->
      <view class="loading-state" v-if="loading && messageList.length === 0">
        <text class="loading-text">加载中...</text>
      </view>
      <!-- 消息列表 -->
      <template v-for="item in messageList">
        <!-- 接收者消息 -->
        <view 
          :key="item.messageId"
          class="message-item message-left"
          v-if="item.senderId !== currentUserId">
          <image 
            :src="otherUserAvatar" 
            mode="aspectFill" 
            class="message-avatar">
          </image>
          <view class="message-content-wrapper">
            <view class="message-content">
              <text>{{ item.messageContent }}</text>
            </view>
            <text class="message-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
        <!-- 发送者消息 -->
        <view 
          :key="item.messageId"
          class="message-item message-right"
          v-else>
          <view class="message-content-wrapper">
            <view class="message-content message-content-right">
              <text>{{ item.messageContent }}</text>
            </view>
            <text class="message-time">{{ formatTime(item.createTime) }}</text>
          </view>
          <image 
            :src="currentUserAvatar" 
            mode="aspectFill" 
            class="message-avatar">
          </image>
        </view>
      </template>
      <!-- 空状态 -->
      <view class="empty-state" v-if="!loading && messageList.length === 0 && sessionId">
        <uni-icons type="chatbubbles" size="80" color="#ddd"></uni-icons>
        <text class="empty-text">暂无消息，开始聊天吧</text>
      </view>
    </scroll-view>

    <!-- 输入栏 -->
    <view class="input-bar">
      <view class="input-wrapper">
        <input 
          class="input" 
          v-model="inputText" 
          placeholder="输入消息..." 
          @confirm="sendMessage"
          confirm-type="send"
          :disabled="loading" />
      </view>
      <view class="send-btn" :class="{ 'disabled': !inputText.trim() || loading }" @click="sendMessage">
        <text>发送</text>
      </view>
    </view>
  </view>
</template>

<script>
  import { getChatMessageList, sendChatMessage, markChatAsRead, getChatSession } from '@/api/message'
  import store from '@/store'
  import { processAvatarUrl } from '@/utils/avatar'
  import { formatRelativeTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        sessionId: null,
        otherUserId: null,
        otherUserAvatar: '/static/images/profile.jpg',
        currentUserId: null,
        currentUserAvatar: '/static/images/profile.jpg',
        messageList: [],
        inputText: '',
        scrollTop: 0,
        loading: false
      }
    },
    onLoad(options) {
      this.currentUserId = store.getters.id
      // 从 store 获取当前用户头像
      this.currentUserAvatar = store.getters.avatar || '/static/images/profile.jpg'
      if (options.sessionId) {
        this.sessionId = options.sessionId
        if (options.userId) {
          this.otherUserId = options.userId
        }
        this.loadMessages()
      } else if (options.userId) {
        this.otherUserId = options.userId
        this.initSession()
      }
    },
    onShow() {
      // 每次显示时刷新当前用户头像（可能已更新）
      this.currentUserAvatar = store.getters.avatar || '/static/images/profile.jpg'
      // 标记消息为已读
      if (this.sessionId) {
        this.markAsRead()
      }
    },
    methods: {
      // 初始化会话
      async initSession() {
        try {
          const res = await getChatSession(this.otherUserId)
          if (res.code === 200 && res.data) {
            this.sessionId = res.data.sessionId || res.data.session_id
            // 处理头像URL，确保正确拼接baseUrl
            const avatar = res.data.otherAvatar || res.data.other_avatar
            this.otherUserAvatar = processAvatarUrl(avatar)
            if (this.sessionId) {
              this.loadMessages()
              return true
            } else {
              console.error('会话ID为空:', res.data)
            }
          } else {
            console.error('初始化会话失败:', res)
          }
          return false
        } catch (error) {
          console.error('初始化会话失败:', error)
          return false
        }
      },
      // 加载消息列表
      async loadMessages() {
        if (!this.sessionId) {
          console.warn('sessionId为空，无法加载消息')
          return
        }
        this.loading = true
        try {
          const res = await getChatMessageList(this.sessionId)
          if (res.code === 200) {
            this.messageList = res.data || []
            // 滚动到底部
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          } else {
            // 静默处理，不显示错误提示
            console.error('加载消息失败:', res.msg)
          }
        } catch (error) {
          // 静默处理，不显示错误提示
          console.error('加载消息异常:', error)
        } finally {
          this.loading = false
        }
      },
      // 发送消息
      async sendMessage() {
        if (!this.inputText.trim()) {
          uni.showToast({ title: '请输入消息内容', icon: 'none' })
          return
        }
        if (!this.otherUserId) {
          console.error('otherUserId为空:', this.otherUserId)
          uni.showToast({ title: '接收者信息错误', icon: 'none' })
          return
        }
        if (!this.currentUserId) {
          console.error('currentUserId为空:', this.currentUserId)
          uni.showToast({ title: '用户信息错误，请重新登录', icon: 'none' })
          return
        }
        
        // 保存消息内容，用于失败时恢复
        const messageContent = this.inputText.trim()
        
        try {
          // 如果还没有sessionId，先初始化会话
          if (!this.sessionId) {
            console.log('sessionId为空，开始初始化会话，otherUserId:', this.otherUserId)
            const initSuccess = await this.initSession()
            // 如果初始化失败，静默处理，不显示错误提示
            if (!initSuccess || !this.sessionId) {
              console.error('初始化会话失败，initSuccess:', initSuccess, 'sessionId:', this.sessionId)
              return
            }
            console.log('会话初始化成功，sessionId:', this.sessionId)
          }
          
          this.inputText = '' // 先清空输入框，提升用户体验
          
          console.log('准备发送消息，receiverId:', this.otherUserId, 'messageContent:', messageContent)
          uni.showLoading({ title: '发送中...' })
          
          const res = await sendChatMessage({
            receiverId: this.otherUserId,
            messageContent: messageContent
          })
          
          uni.hideLoading()
          console.log('发送消息响应:', res)
          
          if (res.code === 200) {
            // 如果发送后还没有sessionId，从响应中获取或重新初始化
            if (!this.sessionId && res.data && res.data.sessionId) {
              this.sessionId = res.data.sessionId
              console.log('从响应中获取sessionId:', this.sessionId)
            }
            // 重新加载消息列表
            this.loadMessages()
          } else {
            // 发送失败，恢复输入框内容
            this.inputText = messageContent
            console.error('发送消息失败，响应:', res)
            uni.showToast({ 
              title: res.msg || '发送失败，请检查网络连接', 
              icon: 'none',
              duration: 3000
            })
          }
        } catch (error) {
          uni.hideLoading()
          console.error('发送消息异常:', error)
          // 恢复输入框内容
          this.inputText = messageContent
          uni.showToast({ 
            title: '发送失败：' + (error.message || '网络错误，请重试'), 
            icon: 'none',
            duration: 3000
          })
        }
      },
      // 标记为已读
      async markAsRead() {
        if (!this.sessionId) return
        try {
          await markChatAsRead(this.sessionId)
        } catch (error) {
          // 静默处理，不显示错误提示
          console.error('标记已读失败:', error)
        }
      },
      // 加载更多
      loadMore() {
        // TODO: 实现分页加载
      },
      // 滚动到底部
      scrollToBottom() {
        this.scrollTop = 99999
      },
      formatTime(time) {
        return formatRelativeTime(time)
      }
    }
  }
</script>

<style scoped>
  .container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #f5f5f5;
    position: relative;
  }

  .message-list {
    flex: 1;
    padding: 20rpx;
    padding-bottom: 180rpx;
    overflow-y: auto;
    box-sizing: border-box;
  }

  .message-item {
    display: flex;
    flex-direction: row;
    margin-bottom: 30rpx;
    align-items: flex-start;
    width: 100%;
  }

  .message-item.message-left {
    justify-content: flex-start;
  }

  .message-item.message-right {
    flex-direction: row;
    justify-content: flex-end;
  }

  .message-avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    flex-shrink: 0;
  }

  /* 接收者消息：头像在左边 */
  .message-item.message-left .message-avatar {
    margin-right: 20rpx;
    margin-left: 0;
  }

  /* 发送者消息：头像在右边 */
  .message-item.message-right .message-avatar {
    margin-left: 20rpx;
    margin-right: 0;
  }

  .message-content-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .message-content {
    background-color: #fff;
    padding: 20rpx;
    border-radius: 10rpx;
    max-width: 60%;
    word-wrap: break-word;
  }

  .message-content-right {
    background-color: #409EFF;
    color: #fff;
    align-self: flex-end;
  }

  .message-time {
    font-size: 22rpx;
    color: #999;
    margin-top: 10rpx;
  }

  .message-item:not(.message-right) .message-time {
    padding-left: 0;
    text-align: left;
  }

  .message-item.message-right .message-time {
    padding-right: 0;
    text-align: right;
  }
  
  .loading-state {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 100rpx 0;
  }
  
  .loading-text {
    font-size: 28rpx;
    color: #999;
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

  .input-bar {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 20rpx;
    padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
    background-color: #fff;
    border-top: 1rpx solid #f0f0f0;
    box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    box-sizing: border-box;
  }

  .input-wrapper {
    flex: 1;
    margin-right: 20rpx;
  }

  .input {
    width: 100%;
    height: 80rpx;
    background-color: #f5f5f5;
    border-radius: 40rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
    border: 1rpx solid #e0e0e0;
    box-sizing: border-box;
  }

  .input:focus {
    border-color: #409EFF;
    background-color: #fff;
  }

  .send-btn {
    background-color: #409EFF;
    color: #fff;
    padding: 20rpx 50rpx;
    border-radius: 40rpx;
    font-size: 28rpx;
    min-width: 120rpx;
    text-align: center;
    box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.3);
  }

  .send-btn.disabled {
    background-color: #ccc;
    color: #999;
    box-shadow: none;
  }

  .send-btn:active {
    opacity: 0.8;
  }
</style>

