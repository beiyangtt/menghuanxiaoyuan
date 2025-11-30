<template>
  <view class="container">
    <!-- 右上角菜单按钮 -->
    <view class="menu-btn" v-if="isOwner" @click="showMenu">
      <uni-icons type="more-filled" size="24" color="#333"></uni-icons>
    </view>
    
    <!-- 任务信息 -->
    <view class="task-info">
      <text class="task-title">{{ taskInfo.taskTitle }}</text>
      <text class="task-reward">¥{{ taskInfo.reward }}</text>
      <view class="task-meta">
        <text class="meta-item">发布时间：{{ formatTime(taskInfo.createTime) }}</text>
        <text class="meta-item">浏览：{{ taskInfo.viewCount || 0 }}次</text>
      </view>
    </view>

    <!-- 任务详情 -->
    <view class="detail-section">
      <text class="section-title">任务详情</text>
      <text class="detail-content">{{ getTaskDetailText() }}</text>
    </view>

    <!-- 发布者信息 -->
    <view class="publisher-section">
      <text class="section-title">发布者信息</text>
      <view class="publisher-info">
        <image :src="publisherInfo.avatar" mode="aspectFill" class="publisher-avatar"></image>
        <view class="publisher-details">
          <text class="publisher-name">{{ publisherInfo.nickName }}</text>
          <text class="publisher-rating">信用：{{ publisherInfo.rating }}</text>
        </view>
        <view class="contact-btn" v-if="!isOwner" @click="contactPublisher">
          <uni-icons type="chat" size="20" color="#fff"></uni-icons>
          <text>联系发布者</text>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view v-if="isOwner" class="complete-btn" :class="{ 'disabled': taskInfo.taskStatus === '2' }" @click="handleComplete">
        {{ taskInfo.taskStatus === '2' ? '已完成' : '完成任务' }}
      </view>
      <view v-else-if="taskInfo.taskStatus === '0'" class="accept-btn" @click="handleAccept">
        接受任务
      </view>
      <view v-else class="accept-btn disabled">
        任务进行中
      </view>
    </view>
  </view>
</template>

<script>
  import { getTaskDetail, deleteTask, completeTask, acceptTask } from '@/api/errand'
  import store from '@/store'
  import { processAvatarUrl } from '@/utils/avatar'
  import { formatDateTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        taskId: null,
        isOwner: false, // 是否是发布者
        currentUserId: null, // 当前登录用户ID
        taskInfo: {
          taskTitle: '',
          reward: '',
          startLocation: '',
          endLocation: '',
          deadline: '',
          taskDesc: '',
          createTime: '',
          taskStatus: '0', // 0待接单 1进行中 2已完成 3已取消
          userId: null, // 发布者ID
          viewCount: 0
        },
        publisherInfo: {
          nickName: '',
          avatar: '/static/images/profile.jpg',
          rating: '5.0'
        }
      }
    },
    onLoad(options) {
      if (options.id) {
        this.taskId = options.id
        this.currentUserId = store.getters.id
        this.loadTaskDetail()
      }
    },
    methods: {
      // 加载任务详情
      async loadTaskDetail() {
        try {
          uni.showLoading({ title: '加载中...' })
          const res = await getTaskDetail(this.taskId)
          if (res.code === 200) {
            const data = res.data
            this.taskInfo = {
              taskTitle: data.taskTitle || '',
              reward: data.taskReward || data.reward || 0,
              startLocation: data.startLocation || '',
              endLocation: data.endLocation || '',
              deadline: data.deadline || data.timeRequirement || '',
              taskDesc: data.taskDesc || '',
              createTime: data.createTime || '',
              taskStatus: data.taskStatus || '0',
              userId: data.userId || null,
              viewCount: data.viewCount || 0
            }
            this.publisherInfo = {
              nickName: data.nickName || '用户',
              avatar: processAvatarUrl(data.avatar),
              rating: '5.0'
            }
            // 判断是否是发布者
            this.isOwner = this.currentUserId && this.taskInfo.userId && 
                          String(this.currentUserId) === String(this.taskInfo.userId)
            
            // 如果任务已完成且不是发布者，不允许访问
            if (this.taskInfo.taskStatus === '2' && !this.isOwner) {
              uni.hideLoading()
              uni.showToast({
                title: '该任务已完成',
                icon: 'none',
                duration: 2000
              })
              setTimeout(() => {
                uni.navigateBack()
              }, 2000)
              return
            }
          } else {
            uni.showToast({
              title: res.msg || '加载失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('加载任务详情失败:', error)
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          })
        } finally {
          uni.hideLoading()
        }
      },
      // 显示菜单
      showMenu() {
        uni.showActionSheet({
          itemList: ['重新编辑', '删除任务'],
          success: (res) => {
            if (res.tapIndex === 0) {
              this.editTask()
            } else if (res.tapIndex === 1) {
              this.deleteTaskConfirm()
            }
          }
        })
      },
      // 编辑任务
      editTask() {
        uni.navigateTo({
          url: `/pages/errand/publish?id=${this.taskId}&mode=edit`
        })
      },
      // 删除任务确认
      deleteTaskConfirm() {
        uni.showModal({
          title: '提示',
          content: '确定要删除该任务吗？删除后无法恢复。',
          success: async (res) => {
            if (res.confirm) {
              try {
                uni.showLoading({ title: '删除中...' })
                const result = await deleteTask(this.taskId)
                if (result.code === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  })
                  setTimeout(() => {
                    uni.navigateBack()
                  }, 1500)
                } else {
                  uni.showToast({
                    title: result.msg || '删除失败',
                    icon: 'none'
                  })
                }
              } catch (error) {
                console.error('删除任务失败:', error)
                uni.showToast({
                  title: '删除失败，请重试',
                  icon: 'none'
                })
              } finally {
                uni.hideLoading()
              }
            }
          }
        })
      },
      contactPublisher() {
        // 跳转到聊天界面
        uni.navigateTo({
          url: `/pages/message/chat?userId=${this.taskInfo.userId}`
        })
      },
      async handleAccept() {
        uni.showModal({
          title: '确认接单',
          content: '一旦确定接受任务，请务必完成，完成后请联系发布者进行确认！',
          success: async (res) => {
            if (res.confirm) {
              try {
                uni.showLoading({ title: '处理中...' })
                const result = await acceptTask(this.taskId)
                if (result.code === 200) {
                  uni.showToast({
                    title: '接单成功',
                    icon: 'success'
                  })
                  this.loadTaskDetail()
                } else {
                  uni.showToast({
                    title: result.msg || '接单失败',
                    icon: 'none'
                  })
                }
              } catch (error) {
                console.error('接单失败:', error)
                uni.showToast({
                  title: '接单失败，请重试',
                  icon: 'none'
                })
              } finally {
                uni.hideLoading()
              }
            }
          }
        })
      },
      async handleComplete() {
        if (this.taskInfo.taskStatus === '2') {
          uni.showToast({
            title: '该任务已完成',
            icon: 'none'
          })
          return
        }
        uni.showModal({
          title: '提示',
          content: '确定该任务已完成吗？完成后将无法修改。',
          success: async (res) => {
            if (res.confirm) {
              try {
                uni.showLoading({ title: '处理中...' })
                const result = await completeTask(this.taskId)
                if (result.code === 200) {
                  uni.showToast({
                    title: '任务完成',
                    icon: 'success'
                  })
                  this.loadTaskDetail()
                } else {
                  uni.showToast({
                    title: result.msg || '操作失败',
                    icon: 'none'
                  })
                }
              } catch (error) {
                console.error('完成任务失败:', error)
                uni.showToast({
                  title: '操作失败，请重试',
                  icon: 'none'
                })
              } finally {
                uni.hideLoading()
              }
            }
          }
        })
      },
      formatTime(time) {
        return formatDateTime(time)
      },
      getTaskDetailText() {
        let text = ''
        if (this.taskInfo.startLocation) {
          text += '起点：' + this.taskInfo.startLocation
        }
        if (this.taskInfo.endLocation) {
          if (text) text += ' '
          text += '终点：' + this.taskInfo.endLocation
        }
        if (this.taskInfo.deadline) {
          if (text) text += ' '
          text += '截止时间：' + this.formatTime(this.taskInfo.deadline)
        }
        if (this.taskInfo.taskDesc) {
          if (text) text += ' '
          text += '任务描述：' + this.taskInfo.taskDesc
        }
        return text
      }
    }
  }
</script>

<style scoped>
  .container {
    background-color: #f5f5f5;
    min-height: 100vh;
    padding-bottom: 120rpx;
  }

  .menu-btn {
    position: fixed;
    top: calc(var(--status-bar-height, 0px) + 12rpx);
    right: 20rpx;
    width: 60rpx;
    height: 60rpx;
    background-color: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
  }

  .task-info {
    background-color: #fff;
    padding: 30rpx;
    margin-top: 20rpx;
  }

  .task-title {
    font-size: 36rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .task-reward {
    font-size: 48rpx;
    color: #f56c6c;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .task-meta {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .meta-item {
    font-size: 24rpx;
    color: #999;
  }

  .detail-section {
    background-color: #fff;
    padding: 30rpx;
    margin-top: 20rpx;
  }

  .section-title {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .detail-content {
    font-size: 28rpx;
    color: #666;
    line-height: 1.8;
    display: block;
  }

  .publisher-section {
    background-color: #fff;
    padding: 30rpx;
    margin-top: 20rpx;
  }

  .publisher-info {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .publisher-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    margin-right: 20rpx;
  }

  .publisher-details {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .publisher-name {
    font-size: 30rpx;
    color: #333;
    margin-bottom: 10rpx;
  }

  .publisher-rating {
    font-size: 24rpx;
    color: #999;
  }

  .contact-btn {
    background-color: #409EFF;
    color: #fff;
    padding: 15rpx 30rpx;
    border-radius: 30rpx;
    display: flex;
    align-items: center;
    font-size: 26rpx;
  }

  .contact-btn text {
    margin-left: 8rpx;
  }

  .bottom-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 20rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.1);
  }

  .accept-btn, .complete-btn {
    flex: 1;
    background-color: #67C23A;
    color: #fff;
    text-align: center;
    padding: 25rpx;
    border-radius: 50rpx;
    font-size: 32rpx;
  }

  .accept-btn.disabled, .complete-btn.disabled {
    background-color: #ccc;
    color: #999;
  }
</style>

